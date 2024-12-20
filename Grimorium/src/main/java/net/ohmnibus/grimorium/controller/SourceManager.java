package net.ohmnibus.grimorium.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.core.os.OperationCanceledException;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.database.BaseDbAdapter;
import net.ohmnibus.grimorium.database.SourceDbAdapter;
import net.ohmnibus.grimorium.database.SpellDbAdapter;
import net.ohmnibus.grimorium.database.SpellProfileDbAdapter;
import net.ohmnibus.grimorium.entity.Source;
import net.ohmnibus.grimorium.entity.Spell;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Ohmnibus on 01/10/2016.
 */

public class SourceManager implements LoaderManager.LoaderCallbacks<SourceManager.ImportResult> {

	private static final String TAG = "SourceManager";

	private static final int MSG_PROGRESS = 0x0001;
	private static final String BUNDLE_PROGRESS_COUNT = "progress.count";

	private static final int MSG_COMPLETED = 0x0002;
	private static final String BUNDLE_COMPLETED_CREATED = "completed.created";
	private static final String BUNDLE_COMPLETED_UPDATED = "completed.updated";
	private static final String BUNDLE_COMPLETED_SKIPPED = "completed.skipped";
	private static final String BUNDLE_COMPLETED_DELETED = "completed.deleted";

	private static final int MSG_ERROR = 0x0003;
	private static final String BUNDLE_ERROR_CODE = "error.code";

	private static final int LOADER_SOURCE_MANAGER = 0x000c;

	private static final int SUPPORTED_FORMAT = 1;

	private static final String BUNDLE_URL = "url";

	private final Context mContext;
	private final LoaderManager mLoaderManager;
	private final MyHandler mHandler;

	public SourceManager(AppCompatActivity activity, OnSourceImportedListener listener) {
		this(activity, LoaderManager.getInstance(activity), listener);
	}

	public SourceManager(Fragment fragment, OnSourceImportedListener listener) {
		this(fragment.getContext(), LoaderManager.getInstance(fragment), listener);
	}

	private SourceManager(Context context, LoaderManager loaderManager, OnSourceImportedListener listener) {
		mContext = context;
		mLoaderManager = loaderManager;
		mHandler = new MyHandler(listener);
		Loader<ImportResult> loader = mLoaderManager.getLoader(LOADER_SOURCE_MANAGER);
		if (loader != null) {
			//An operation is already running.
			//Attach to it.

			//This will update the handler
			((SourceAsyncTaskLoader)loader).setHandler(mHandler);

			//This will update the callback to this instance
			mLoaderManager.initLoader(LOADER_SOURCE_MANAGER, null, this);
		}
	}

	public void startDownload(String url) {
		//TODO: Check if another import is pending
		Bundle data = new Bundle();
		data.putString(BUNDLE_URL, url);
		mLoaderManager.restartLoader(LOADER_SOURCE_MANAGER, data, this);
	}

	public void cancelDownload() {
		mLoaderManager.destroyLoader(LOADER_SOURCE_MANAGER);
	}

	public static AlertDialog getErrorDialog(Context context, int errorCode) {
		final String message;
		switch (errorCode) {
			case OnSourceImportedListener.ERR_NO_CONNECTION:
				message = context.getString(R.string.lbl_source_error_no_connection);
				break;
			case OnSourceImportedListener.ERR_CANNOT_READ:
				message = context.getString(R.string.lbl_source_error_cannot_read);
				break;
			case OnSourceImportedListener.ERR_FORMAT_NOT_SUPPORTED:
				message = context.getString(R.string.lbl_source_error_format_unsupported);
				break;
			case OnSourceImportedListener.ERR_FORMAT_NOT_VALID:
				message = context.getString(R.string.lbl_source_error_format_invalid);
				break;
			case OnSourceImportedListener.ERR_CANNOT_CONNECT:
				message = context.getString(R.string.lbl_source_error_cannot_connect);
				break;
			case OnSourceImportedListener.ERR_STALE_DATA:
				message = context.getString(R.string.lbl_source_error_stale_data);
				break;
			case OnSourceImportedListener.ERR_INVALID_URL:
				message = context.getString(R.string.lbl_source_error_invalid_url);
				break;
			case OnSourceImportedListener.ERR_GENERIC:
			default:
				message = context.getString(R.string.lbl_source_error);
				break;
		}
		AlertDialog.Builder bld = new AlertDialog.Builder(context);
		bld.setTitle(R.string.lbl_source);
		bld.setMessage(message);
		bld.setPositiveButton(R.string.lbl_ok, null);
		return bld.create();
	}

	@NonNull
	@Override
	public Loader<ImportResult> onCreateLoader(int id, Bundle args) {
		String url = null;
		if (args != null) {
			url = args.getString(BUNDLE_URL);
		}
		return new SourceAsyncTaskLoader(mContext, url, mHandler);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<ImportResult> loader, ImportResult data) {
		if (data == null) //Shortcut
			return;

		Message msg = new Message();
		Bundle bundle = new Bundle();

		if (data.status == OnSourceImportedListener.ERR_NONE) {
			msg.what = MSG_COMPLETED;
			bundle.putInt(BUNDLE_COMPLETED_CREATED, data.insertCount);
			bundle.putInt(BUNDLE_COMPLETED_UPDATED, data.updateCount);
			bundle.putInt(BUNDLE_COMPLETED_SKIPPED, data.skippedCount);
			bundle.putInt(BUNDLE_COMPLETED_DELETED, data.deleteCount);
		} else {
			msg.what = MSG_ERROR;
			bundle.putInt(BUNDLE_ERROR_CODE, data.status);
		}

		msg.setData(bundle);

		mHandler.sendMessage(msg);

		cancelDownload();
	}

	@Override
	public void onLoaderReset(@NonNull Loader<ImportResult> loader) {
		//NOOP
	}

	private static class MyHandler extends Handler {

		private final WeakReference<OnSourceImportedListener> mListener;

		public MyHandler(OnSourceImportedListener listener) {
			super(Looper.myLooper());
			mListener = new WeakReference<>(listener);
		}

		@Override
		public void handleMessage(Message msg) {

			OnSourceImportedListener listener = mListener.get();
			if (listener == null) //Shortcut
				return;

			Bundle data = msg.getData();
			switch (msg.what) {
				case MSG_PROGRESS:
					int count = -1;
					if (data != null && data.containsKey(BUNDLE_PROGRESS_COUNT)) {
						count = data.getInt(BUNDLE_PROGRESS_COUNT);
					}
					if(count >= 0){
						listener.onSourceImportProgress(count);
					}
					break;
				case MSG_COMPLETED:
					int created = 0;
					int updated = 0;
					int skipped = 0;
					int deleted = 0;
					if (data != null) {
						created = data.getInt(BUNDLE_COMPLETED_CREATED, 0);
						updated = data.getInt(BUNDLE_COMPLETED_UPDATED, 0);
						skipped = data.getInt(BUNDLE_COMPLETED_SKIPPED, 0);
						deleted = data.getInt(BUNDLE_COMPLETED_DELETED, 0);
					}
					listener.onSourceImported(created, updated, skipped, deleted);
					break;
				case MSG_ERROR:
					int status = OnSourceImportedListener.ERR_CANNOT_READ;
					if (data != null) {
						status = data.getInt(BUNDLE_ERROR_CODE, status);
					}
					listener.onSourceImportFailed(status);
			}
		}
	}

	protected static class ImportResult {
		int status = OnSourceImportedListener.ERR_NONE;
		int insertCount = 0;
		int updateCount = 0;
		int deleteCount = 0;
		int skippedCount = 0;
	}

	private static class SourceAsyncTaskLoader extends AsyncTaskLoader<ImportResult> {

		private static final int PROGRESS_STEP = 5;
		private final String mRawUrl;
		private final Gson mGson;
		private Handler mHandler;
		private boolean mResetPending;
		private int mProgressUpdateCount;

		public SourceAsyncTaskLoader(Context context, String url, Handler handler) {
			super(context);
			mRawUrl = url;
			mHandler = handler;
			mResetPending = false;
			mGson = new GsonBuilder()
					.create();
		}

		public void setHandler(Handler handler) {
			mHandler = handler;
		}

		@Override
		protected boolean onCancelLoad() {
			return super.onCancelLoad();
		}

		@Override
		protected void onStartLoading() {
			mResetPending = false;
			forceLoad();
		}

		@Override
		protected void onReset() {
			super.onReset();
			mResetPending = true;
		}

		@Override
		public ImportResult loadInBackground() {
			ImportResult retVal;
			mProgressUpdateCount = 0;
			try {
				URL url = new URL(mRawUrl);
				retVal = privateImport(url);
			} catch (MalformedURLException e) {
				retVal = new ImportResult();
				retVal.status = OnSourceImportedListener.ERR_INVALID_URL;
			}

			return retVal;
		}

		private void publishProgress(int progress) {
			if (mHandler != null) {

				mProgressUpdateCount++;
				if (mProgressUpdateCount % PROGRESS_STEP != 0)
					return;

				Bundle data = new Bundle();
				data.putInt(BUNDLE_PROGRESS_COUNT, progress);

            	/* Creating a message */
				Message msg = new Message();
				msg.setData(data);
				msg.what = MSG_PROGRESS;

            	/* Sending the message */
				mHandler.sendMessage(msg);

			}
		}

		private ImportResult privateImport(URL url) {
			ImportResult retVal;

			//Check connectivity
			ConnectivityManager connMgr = (ConnectivityManager)
					getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean isConnected;
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			//noinspection deprecation
			isConnected = (networkInfo != null && networkInfo.isConnected());
			if (!isConnected) {
				//Shortcut
				retVal = new ImportResult();
				retVal.status = OnSourceImportedListener.ERR_NO_CONNECTION;
				return retVal;
			}

			//Connect and read data
			HttpURLConnection conn = null;

			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();
				Log.d(TAG, "privateImport: The response is: " + response);
				InputStream stream = conn.getInputStream();

				retVal = privateImport(url, stream);

			} catch (IOException e) {
				retVal = new ImportResult();
				retVal.status = OnSourceImportedListener.ERR_CANNOT_CONNECT;
				Log.e(TAG, "privateImport", e);
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			return retVal;
		}

		/*
	{
	  "format": 1,
	  "version": 1,
	  "name": "Wizard Spells Collection",
	  "nameSpace": "collection.spell.wizard",
	  "description": "Description of this set. May contain <b>HTML</b>.",
	  "spells": [
		{ }
	  ]
	}
		 */
		private static final String FIELD_FORMAT = "format";
		private static final String FIELD_VERSION = "version";
		private static final String FIELD_NAME = "name";
		private static final String FIELD_NAMESPACE = "nameSpace";
		private static final String FIELD_DESCRIPTION = "description";
		private static final String FIELD_SPELLS = "spells";

		private ImportResult privateImport(URL url, InputStream stream) {
			ImportResult retVal = new ImportResult();
			Source source = new Source();
			String field;
			boolean isFormat = false;
			boolean isVersion = false;
			boolean isNameSpace = false;

			retVal.status = OnSourceImportedListener.ERR_NONE;

			try {
				source.setUrl(url.toString());
				InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
				JsonReader reader = new JsonReader(isr);

				reader.beginObject();

				while (reader.hasNext()) {
					if (mResetPending) {
						throw new OperationCanceledException();
					}
					field = reader.nextName();
					switch (field) {
						case FIELD_FORMAT:
							//Consume format value
							if (reader.nextInt() > SUPPORTED_FORMAT) {
								//Format version not supported
								throw new UnsupportedFormatException();
							}
							isFormat = true;
							break;
						case FIELD_VERSION:
							//Version
							source.setVersion(reader.nextInt());
							isVersion = true;
							break;
						case FIELD_NAME:
							//Name
							source.setName(reader.nextString());
							break;
						case FIELD_NAMESPACE:
							//Namespace
							source.setNameSpace(reader.nextString());
							isNameSpace = true;
							break;
						case FIELD_DESCRIPTION:
							//Description
							source.setDesc(reader.nextString());
							break;
						case FIELD_SPELLS:
							if (!(isFormat && isNameSpace && isVersion)) {
								//Some field is missing or defined after the spell list
								throw new IllegalStateException();
							}

							importSpellList(retVal, reader, source);

							break;
					}
				}

				reader.endObject();
				reader.close();
			} catch (UnsupportedFormatException e) {
				retVal.status = OnSourceImportedListener.ERR_FORMAT_NOT_SUPPORTED;
				Log.e(TAG, "privateImport", e);
			} catch (OlderDataVersionException e) {
				retVal.status = OnSourceImportedListener.ERR_STALE_DATA;
				Log.e(TAG, "privateImport", e);
			} catch (JsonSyntaxException | IllegalStateException | UnsupportedEncodingException e) {
				retVal.status = OnSourceImportedListener.ERR_FORMAT_NOT_VALID;
				Log.e(TAG, "privateImport", e);
			} catch (IOException e) {
				retVal.status = OnSourceImportedListener.ERR_CANNOT_READ;
				Log.e(TAG, "privateImport", e);
			} catch (OperationCanceledException e) {
				retVal.status = OnSourceImportedListener.ERR_NONE;
				Log.e(TAG, "privateImport", e);
			} catch (Exception e) {
				retVal.status = OnSourceImportedListener.ERR_GENERIC;
				Log.e(TAG, "privateImport", e);
			}

			return retVal;
		}

		private void importSpellList(ImportResult retVal, JsonReader reader, Source source) throws IOException {
			boolean isNewSource;

			try {
				final SourceDbAdapter srcDbAdapter = SourceDbAdapter.getInstance();
				final SpellDbAdapter spellDbAdapter = SpellDbAdapter.getInstance();
				Source testSource = srcDbAdapter.getByNamespace(source.getNameSpace());

				BaseDbAdapter.beginTransaction();

				if (testSource != null) {
					if (testSource.getVersion() >= source.getVersion()) {
						//Source is older than the one already imported
						throw new OlderDataVersionException();
					}
					isNewSource = false;
					source.setId(testSource.getId());
					srcDbAdapter.update(source.getNameSpace(), source, true);
					spellDbAdapter.initSource(source.getId(), true);
				} else {
					//Insert new source
					isNewSource = true;
					source.setId(srcDbAdapter.insert(source, true));
				}

				final long srcId = source.getId();
				final String srcNs = source.getNameSpace();

				final SpellProfileDbAdapter starDbAdapter = SpellProfileDbAdapter.getInstance();

				int spellIndex = 0;
				long spellId;
				int spellUid;
				boolean isNewSpell;

				reader.beginArray();
				while (reader.hasNext()) {
					if (mResetPending) {
						throw new OperationCanceledException();
					}

					Spell spell = mGson.fromJson(reader, Spell.class);
					spell.setSourceId(srcId);
					spell.initBitfields();
					int setStatus = spellDbAdapter.set(spell, true);
					if (setStatus == SpellDbAdapter.SET_CREATED) {
						isNewSpell = true;
						retVal.insertCount++;
					} else if (setStatus == SpellDbAdapter.SET_UPDATED) {
						isNewSpell = false;
						retVal.updateCount++;
					} else {
						retVal.skippedCount++;
						Log.w(TAG, "privateImport: Cannot save spell " + spellIndex + ": " + spell.getName());
						continue;
					}

					spellId = spell.getId();
					spellUid = spell.getUid();

					publishProgress(retVal.insertCount + retVal.updateCount);

					if (isNewSource || isNewSpell) {
						//Resync star data, if exist
						starDbAdapter.syncStar(spellUid, srcNs, spellId, true);
					}

					spellIndex++;
				}
				reader.endArray();

				retVal.deleteCount = spellDbAdapter.purgeDeletedBySource(srcId, true);

				BaseDbAdapter.setTransactionSuccessful();
//			} catch (Exception ex) {
//				Log.e(TAG, "privateImport", ex);
//				throw ex;
			} finally {
				BaseDbAdapter.endTransaction();
			}
		}
	}

	private static class UnsupportedFormatException extends UnsupportedOperationException { }

	private static class OlderDataVersionException extends IllegalStateException { }

	/**
	 * Interface definition for a callback to be invoked
	 * when a source is imported.
	 */
	public interface OnSourceImportedListener {
		/** No error */
		int ERR_NONE = 0;
		/** No connection */
		int ERR_NO_CONNECTION = 1;
		/** Generic error reading data */
		int ERR_CANNOT_READ = 2;
		/** Format not supported - need to update app */
		int ERR_FORMAT_NOT_SUPPORTED = 3;
		/** Error in file */
		int ERR_FORMAT_NOT_VALID = 4;
		/** Connection available but still cannot connect (404) */
		int ERR_CANNOT_CONNECT = 5;
		/** A newer version was already imported */
		int ERR_STALE_DATA = 6;
		/** The URI provided is not valid */
		int ERR_INVALID_URL = 7;
		/** Generic error */
		int ERR_GENERIC = 8;

		/**
		 * Called when a source has been imported.
		 */
		void onSourceImported(int created, int updated, int skipped, int deleted);

		/**
		 * Called while importing a source.
		 */
		void onSourceImportProgress(int itemReadSoFar);

		/**
		 * Called when an error occurred during source import.
		 * @param errorCode Error code, defined as one of the constant of this interface.
		 */
		void onSourceImportFailed(int errorCode);
	}

}
