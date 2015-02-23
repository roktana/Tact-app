package com.tactile.tact.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.common.collect.Sets;
import com.tactile.tact.activities.LandingActivity;
import com.tactile.tact.activities.OnBoardingDoQuestionActivity;
import com.tactile.tact.R;
import com.tactile.tact.activities.TactApplication;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.database.DatabaseContentProvider;
import com.tactile.tact.database.DatabaseManager;
import com.tactile.tact.services.TactCUDLSyncService;
import com.tactile.tact.services.events.EventAddSourceOnboarding;
import com.tactile.tact.services.events.EventAddSourceSuccess;
import com.tactile.tact.services.events.EventUnZipError;
import com.tactile.tact.services.events.EventUnZipSuccess;
import com.tactile.tact.services.network.APIResponse;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

import de.greenrobot.event.EventBus;

public class Utils {
    public static final String TACT_PREFERENCES = "tact_credentials";
    public static final String TACT_ACCOUNT_AUTH = "tact_auth";
    public static final String TACT_ACCOUNT_IS_LOGGED_IN = "tact_logged_in";


//    /**
//     * Build a new notification for TactApp
//     *
//     */
//    public static void showNotification(Utils.NotificationType type){
//
//        Context context     = TactApplication.getAppContext();
//        Bitmap largeIcon    = ((BitmapDrawable) context.getApplicationInfo().loadIcon(context.getPackageManager())).getBitmap();
//        DateTime date       = DateTime.now();
//        String title        = "";
//        String message      = "";
//        int smallIcon       = 0;
//        Intent intent       = null;
//
//
//        switch(type){
//            case INITIAL_DB_FINISH:
//                smallIcon   = R.drawable.sync_arrows2x;
//                intent      = new Intent(context, HomeActivity.class);
//                title       = "Synchronization has finished";
//                message     = "You can start using Tact now!";
//            break;
//        }
//
//
//        PendingIntent tapAction = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        Notification notification = new Notification.Builder(context)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setSmallIcon(smallIcon)
//                .setLargeIcon(largeIcon)
//                .setContentIntent(tapAction)
//                .setWhen(date.getMillis())
//                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                .setLights(Color.RED, 3000, 3000)
//                .setNumber(2)
//                .setPriority(3)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setAutoCancel(true)
//                .build();
//        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        nManager.cancelAll();
//        nManager.notify(0,notification);
//    }



    /**
     * Get the current date time
     * @return the formated date
     */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static long convertTimeZone(long lngDate, String fromTimeZone,
                                       String toTimeZone) {
        Calendar fromTime = Calendar.getInstance();
        fromTime.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
        fromTime.setTimeInMillis(lngDate);
        Calendar toTime = new GregorianCalendar(
                TimeZone.getTimeZone(toTimeZone));
        toTime.set(Calendar.DATE, fromTime.get(Calendar.DATE));
        toTime.set(Calendar.MONTH, fromTime.get(Calendar.MONTH));
        toTime.set(Calendar.YEAR, fromTime.get(Calendar.YEAR));
        toTime.set(Calendar.HOUR_OF_DAY, fromTime.get(Calendar.HOUR_OF_DAY));
        toTime.set(Calendar.MINUTE, fromTime.get(Calendar.MINUTE));
        toTime.set(Calendar.SECOND, fromTime.get(Calendar.SECOND));
        toTime.set(Calendar.MILLISECOND, fromTime.get(Calendar.MILLISECOND));
        return toTime.getTimeInMillis();
    }

    /**
     * Check if the android device has a caller app
     *
     * @return true/false
     */
    public static boolean hasTelephonyApp(Context context) {

        boolean hasTelephony = false;
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        if (tm.getPhoneType() != 0) {
            hasTelephony = true;
        }

        return hasTelephony || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static void hideKeyboard(View view, Context context ){
        //Hide keyboard if is visible
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    //*********************  API RESPONSE METHODS  *********************\\

    /**
     * Clean the numbers before the json stringified response
     *
     * @param responseString the response
     * @return matched responseString
     */
    public static String cleanResponseText(String responseString) {
        Pattern p = Pattern.compile("(\\d+)\\{");
        Matcher m = p.matcher(responseString);
        if (m.find()) {
            return responseString.substring(m.end(1), responseString.length());
        }

        return responseString;
    }

    /**
     * This method extracts the errorMsg of APIResponse object
     * @param param the response to parse
     * @return the json parsed in the response
     * @throws JSONException
     */
    public static String getResponseError(APIResponse param) throws JSONException {
        String stringResult = Utils.cleanResponseText(param.getResult().getErrorString());
        return new JSONObject(stringResult).getString("error_msg");
    }



    //*********************  LOG METHODS  *********************\\

    /**
     * Prints the string
     * @param str the string to be printed
     */
    public static void Log (String str) {
//        if(APIConstants.getSharedInstance().showDebugSettings())
            System.out.println(str);
    }

    /**
     * Prints the string representation of the boolean {@code b}.
     */
    public static void Log(boolean b) {
        Utils.Log(String.valueOf(b));
    }

    /**
     * Prints the string representation of the character array {@code chars} followed by a newline.
     */
    public static void Log(char[] chars) {
        Utils.Log(new String(chars, 0, chars.length));
    }

    /**
     * Prints the string representation of the char {@code c} followed by a newline.
     */
    public static void Log(char c) {
        Utils.Log(String.valueOf(c));
    }

    /**
     * Prints the string representation of the double {@code d} followed by a newline.
     */
    public static void Log(double d) {
        Utils.Log(String.valueOf(d));
    }

    /**
     * Prints the string representation of the float {@code f} followed by a newline.
     */
    public static void Log(float f) {
        Utils.Log(String.valueOf(f));
    }

    /**
     * Prints the string representation of the int {@code i} followed by a newline.
     */
    public static void Log(int i) {
        Utils.Log(String.valueOf(i));
    }

    /**
     * Prints the string representation of the long {@code l} followed by a newline.
     */
    public static void Log(long l) {
        Utils.Log(String.valueOf(l));
    }

    /**
     * Prints the string representation of the Object {@code o}, or {@code "null"},
     * followed by a newline.
     */
    public static void Log(Object o) {
        Utils.Log(String.valueOf(o));
    }



    //*********************  FILES METHODS  *********************\\

    /**
     * Create a file with a given file name and any string data
     * @param filename the name of the new file
     * @param data the data we want to store in the file
     */
    public static void createFile(Context context, String filename, String data) throws Exception{
        File file_path = new File(context.getFilesDir().getAbsolutePath());
        if (!file_path.exists() || !file_path.isDirectory()){
            file_path.mkdir();
        }

        File file = new File(file_path, filename);
        file.setReadable(true, false);
        file.setWritable(true, false);
        file.setExecutable(true, false);

        FileOutputStream outputStream;

        outputStream = new FileOutputStream(file);
        outputStream.write(data.getBytes());
        outputStream.close();
    }

    /**
     * Delete a file
     * @param fileName the name of the file to delete
     * @return if the delete operation success or not
     */
    public static boolean deleteFile(Context context, String fileName) {
        File file_path = new File(context.getFilesDir().getAbsolutePath());
        if (!file_path.exists() || !file_path.isDirectory()){
            file_path.mkdir();
        }

        File localStorageFile = new File(file_path, fileName);
        return localStorageFile.delete();
    }

    /**
     * Verify if a file exists
     * @param filename the name of the file
     * @return if the file exists (true/false)
     */
    public static Boolean existsFile(Context context, String filename){
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + filename);
        return file.exists();
    }

    public static Boolean existsDatabase(String name){
        File file = new File(TactApplication.getDatabasePath() + "/" + name);
        return file.exists();
    }


    /**
     * Zip two files
     * @param zipFileName the name of the zip
     * @param filename1 the name of the first file to zip
     * @param filename2 the name of the second file to zip
     * @throws Exception
     */
    public static void zip(Context context, String zipFileName, String filename1, String filename2) throws Exception{
        String files_path           = context.getFilesDir().getAbsolutePath();
        try {
            if (existsFile(context, zipFileName)){
                deleteFile(context, zipFileName);
            }

            // Initiate ZipFile object with the path/name of the zip file.
            ZipFile zipFile = new ZipFile(files_path + "/" + zipFileName);


            // Build the list of files to be added in the array list
            // Objects of type File have to be added to the ArrayList
            ArrayList filesToAdd = new ArrayList();
            filesToAdd.add(new File(files_path, filename1));
            filesToAdd.add(new File(files_path, filename2));

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc.
            ZipParameters parameters = new ZipParameters();

            // set compression method to store compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);

            // Set the compression level. This value has to be in between 0 to 9
            //parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            zipFile.createZipFile(filesToAdd, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unzip a file, deleting the zip if it is necessary. When finalize an event is thrown
     * @param originPath - Path to the zip file
     * @param fileName - Name of the zip file
     * @param destinationPath - Path to decompress the zip file to
     * @param deleteZip - If True delete the zip file after decompression, else keep the file
     */
    public static void unZip(String originPath, String fileName, String destinationPath, boolean deleteZip){
        File archive = new File(originPath + "/" + fileName);
        try {
            java.util.zip.ZipFile zipfile = new java.util.zip.ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();

                if (entry.isDirectory()) {
                    createDir(new File(destinationPath, entry.getName()));
                    break;
                }

                File outputFile = new File(destinationPath, entry.getName());
                if (!outputFile.getParentFile().exists()) {
                    createDir(outputFile.getParentFile());
                }

                Log("Extracting: " + entry);
                BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

                try {
                    IOUtils.copy(inputStream, outputStream);
                } finally {
                    outputStream.close();
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            Log("Error while extracting file " + archive);
            EventBus.getDefault().post(new EventUnZipError(e.getMessage()));
        }

        if (deleteZip){
            archive.delete();
        }
        EventBus.getDefault().post(new EventUnZipSuccess());
    }

    public static void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        Log("Creating dir " + dir.getName());
        if (!dir.mkdirs()) {
            throw new RuntimeException("Can not create dir " + dir);
        }
    }

    /**
     -     * method is used for checking valid email id format.
     -     *
     -     * @param email
     -     * @return boolean true for valid false for invalid
     +     * Get the login credentials for the current tact user, stored in the shared preferences
     +     * @return the authorization for the user account
     */

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }

    /**
     * Check Internet connectivity
     * @param cntx - Instance of the context where the method is called from
     * @return If there is Internet connection or not
     */
    public static boolean isOnline(Context cntx) {
        ConnectivityManager cm = (ConnectivityManager)cntx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Get the numbers of days between two dates
     * @param startDate - Start date
     * @param endDate - End date
     * @return Number of days between the dates
     */
    public static long daysBetween(Calendar startDate, Calendar endDate)
    {
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    /**
     * Get the string of today date formated by parameter
     * @param pDateFormat - Date format to build string. Ex:
     * 				     yyyy-MM-dd 1969-12-31
    yyyy-MM-dd 1970-01-01
    yyyy-MM-dd HH:mm 1969-12-31 16:00
    yyyy-MM-dd HH:mm 1970-01-01 00:00
    yyyy-MM-dd HH:mmZ 1969-12-31 16:00-0800
    yyyy-MM-dd HH:mmZ 1970-01-01 00:00+0000
    yyyy-MM-dd HH:mm:ss.SSSZ 1969-12-31 16:00:00.000-0800
    yyyy-MM-dd HH:mm:ss.SSSZ 1970-01-01 00:00:00.000+0000
    yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
    yyyy-MM-dd'T'HH:mm:ss.SSSZ 1970-01-01T00:00:00.000+0000
     * @return String with today date formated
     */
    public static String getTodayFormated(String pDateFormat)
    {
        SimpleDateFormat todayFormater = new SimpleDateFormat(pDateFormat);
        Calendar calendar = Calendar.getInstance();
        return todayFormater.format(calendar.getTime());
    }

    public static Calendar getDateAddDaysFromToday(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar;
    }

    public static String getDateStringFormated(Calendar pCalendar, String pFormat) {
        SimpleDateFormat todayFormater = new SimpleDateFormat(pFormat);
        return todayFormater.format(pCalendar.getTime());
    }

    /**
     * Return date from string
     * @param pDate - String with the date to parse
     * @return Calendar type with the parsed date with format "yyyy-MM-dd"
     */
    public static Calendar getDateFromString(String pDate)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(formater.parse(pDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static long getDateInGMT(long date) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String result = isoFormat.format(date);
        return Utils.getDateFromString(result, "yyyy-MM-dd'T'HH:mm:ss").getTimeInMillis();
    }

    public static long covertDateInGMT(long date) {
        Calendar calendarLocal = Calendar.getInstance();
        Calendar calendarGMT = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendarLocal.setTimeInMillis(date);
        calendarGMT.set(Calendar.YEAR, calendarLocal.get(Calendar.YEAR));
        calendarGMT.set(Calendar.MONTH, calendarLocal.get(Calendar.MONTH));
        calendarGMT.set(Calendar.DAY_OF_MONTH, calendarLocal.get(Calendar.DAY_OF_MONTH));
        return calendarGMT.getTimeInMillis();
    }

    public static long covertDateGMTToLocale(long date) {
        Calendar calendarLocal = Calendar.getInstance();
        Calendar calendarGMT = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendarLocal.setTimeInMillis(date);
        calendarLocal.set(Calendar.YEAR, calendarGMT.get(Calendar.YEAR));
        calendarLocal.set(Calendar.MONTH, calendarGMT.get(Calendar.MONTH));
        calendarLocal.set(Calendar.DAY_OF_MONTH, calendarGMT.get(Calendar.DAY_OF_MONTH));
        return calendarLocal.getTimeInMillis();
    }

    public static Calendar getDateFromString(String pDate, String pFormat)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat(pFormat);
        try {
            calendar.setTime(formater.parse(pDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static Date getDateFromTimestamp(long timestamp) {
        try {
            timestamp = timestamp*1000;
            return new Date(timestamp);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStringDateFormatted(int year, int month, int day, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return Utils.getDateStringFormated(calendar, format);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, int currentWidth, int currentHeight) {
        // Raw height and width of image
        final int height = currentHeight;
        final int width = currentWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            inSampleSize *= 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight, int orientation) {

        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            if (orientation == 90 || orientation == 270) {
                options.inSampleSize = Utils.calculateInSampleSize(options, reqWidth, reqHeight, options.outHeight, options.outWidth);
            } else {
                options.inSampleSize = Utils.calculateInSampleSize(options, reqWidth, reqHeight, options.outWidth, options.outHeight);
            }

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap outBitmap = BitmapFactory.decodeFile(path, options);

			/*
			 * if the orientation is not 0 (or -1, which means we don't know), we
			 * have to do a rotation.
			 */
            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                outBitmap = Bitmap.createBitmap(outBitmap, 0, 0, outBitmap.getWidth(), outBitmap.getHeight(), matrix, true);
            }

            return outBitmap;

        } catch (Exception e) {
            return null;
        }


    }

    public static int[] getDisplaySize(Context context) {
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            // Calculate inSampleSize
            int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, options.outWidth, options.outHeight);
            options.inSampleSize = inSampleSize;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (OutOfMemoryError e) {
            return Utils.decodeSampledBitmapFromResourceFixedSampleSize(res, resId, 2);
        }
    }

    public static Bitmap decodeSampledBitmapFromResourceFixedSampleSize(Resources res, int resId, int fixedSampleSize) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = fixedSampleSize;
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (OutOfMemoryError e) {
            return Utils.decodeSampledBitmapFromResourceFixedSampleSize(res, resId, fixedSampleSize + 1);
        }
    }

    public static int getOrientation(Context context, Uri photoUri) {
        try {
			/* it's on the external media. */
            Cursor cursor = context.getContentResolver().query(photoUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

            if (cursor == null || cursor.getCount() != 1) {
                return -1;
            }

            cursor.moveToFirst();
            return cursor.getInt(0);
        } catch (Exception e) {
            return 0;
        }

    }

    public static void setConnectionErrorMessage(Context ctx){
        try {
            TactDialogHandler dialog = new TactDialogHandler(ctx);
            dialog.dismiss();
            dialog.showInformation(ctx.getString(R.string.connection_error));
        }
        catch (Exception e){}
    }

    public static void setConnectionTroubleMessage(Context ctx){
        TactDialogHandler dialog = new TactDialogHandler(ctx);
        dialog.dismiss();
        dialog.showInformation(ctx.getString(R.string.connection_trouble));
    }

    /**
     * Converts JSONArray to a ArrayList
     */
    public static ArrayList<String> jsonArrayToArrayList(JSONArray array) {
        ArrayList<String> itemList = new ArrayList<String>();

        for (int i = 0; i<array.length(); i++) {
            try {
                itemList.add(array.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return itemList;
    }

    /**
     * Start Do Question Activity
     * @param view the view that calls the Do Question Activity
     */
    public static void doQuestion(View view){
        Intent intent = new Intent(view.getContext(), OnBoardingDoQuestionActivity.class);
        view.getContext().startActivity(intent);
    }

    public static enum NotificationType {
        PENDING_SYNC,
        INITIAL_DB_FINISH,
        SETTINGS,
        OPPORTUNITIES,
        NOTES,
        CONTACTS,
        CALENDAR,
        EMAIL
    }

    /**
     * Get from database if the user sync a Salesforce account
     * @param resolver
     * @return true/false
     */
    public static boolean isSalesForceSync(ContentResolver resolver){
        return DatabaseContentProvider.getIsSynced(resolver, "salesforce") || TactSharedPrefController.isSharedPreferenceExist("salesforceSync");
    }

    /**
     * Get from database if the user sync a EXCHANGE account
     * @param resolver
     * @return true/false
     */
    public static boolean isExchangeSync(ContentResolver resolver){
        return DatabaseContentProvider.getIsSynced(resolver, "exchange") || TactSharedPrefController.isSharedPreferenceExist("exchangeSync");
    }

    /**
     * Get from database if the user sync a GOOGLE account
     * @param resolver
     * @return true/false
     */
    public static boolean isGoogleSync(ContentResolver resolver){
        return DatabaseContentProvider.getIsSynced(resolver, "google") || TactSharedPrefController.isSharedPreferenceExist("googleSync");
    }

    /**
     * Get the username of the salesforce account
     * @param resolver
     * @return
     */
    public static String getSalseforceUsername(ContentResolver resolver){
        String username = DatabaseContentProvider.getUsername(resolver, "salesforce");
        if (username.equals("")){
            TactSecuredPreferences preferences = TactApplication.getInstance().getSecuredPrefs();
            username = preferences.getString("salesforceSync");
        }
        return username != null ? username : "";
    }

    /**
     * Get the username of the exchange account
     * @param resolver
     * @return
     */
    public static String getExchangeUsername(ContentResolver resolver){
        String username = DatabaseContentProvider.getUsername(resolver, "exchange");
        if (username.equals("")){
            TactSecuredPreferences preferences = TactApplication.getInstance().getSecuredPrefs();
            username = preferences.getString("exchangeSync");
        }
        return username != null ? username : "";
    }

    /**
     * Get the username of the google account
     * @param resolver
     * @return
     */
    public static String getGoogleUsername(ContentResolver resolver){
        String username = DatabaseContentProvider.getUsername(resolver, "google");
        if (username.equals("")){
            TactSecuredPreferences preferences = TactApplication.getInstance().getSecuredPrefs();
            username = preferences.getString("googleSync");
        }
        return username != null ? username : "";
    }

    /**
     * Get from database if the user sync local sources
     * @param resolver
     * @return true/false
     */
    public static boolean isContactSync(ContentResolver resolver){
        return DatabaseContentProvider.getIsSynced(resolver, "fullcontact");
    }

    /**
     * Get salesforce account id from database
     * @param resolver
     * @return id
     */
    public static String getSalesforceAccountId(ContentResolver resolver){
        return DatabaseContentProvider.getAccountId(resolver, "salesforce");
    }

    /**
     * Get exchange account id from database
     * @param resolver
     * @return id
     */
    public static String getExchangeAccountId(ContentResolver resolver){
        return DatabaseContentProvider.getAccountId(resolver, "exchange");
    }

    /**
     * Get google account id from database
     * @param resolver
     * @return id
     */
    public static String getGoogleAccountId(ContentResolver resolver){
        return DatabaseContentProvider.getAccountId(resolver, "google");
    }

    /**
     * Get the tact db dataversion from the file db-metadata
     * @return the db dataversion
     */
    public static String getXTactDbDataVersion(Context context) {
        String current_path = "databases/";
        String file_name = "db-metadata";
        if (!Utils.existsFile(context, current_path + file_name)) {
            return "-1";
        }
        else {
            try {
                String path = context.getFilesDir().getAbsolutePath() + "/";
                StringBuilder text = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(path + current_path + file_name));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
                JSONObject obj = new JSONObject(new String(text));
                return obj.getString("db_data_version");

            } catch (Exception e) {
                Utils.Log(e.getMessage());
                return "-1";
            }

        }
    }

    /**
     * Release all variables of sync flow because we recently downloaded the new db
     */
    public static void releaseReSync(){
        TactSharedPrefController.removeSharedPreference("ReSync");
        TactSharedPrefController.removeSharedPreference("onGoingSync");
        for (String account: new String[]{"google", "exchange", "salesforce"}) {
            TactSharedPrefController.removeSharedPreference(account + "Sync");
        }

    }

    public static void logout(Context context){
        //1 - Clear all shared preferences
        //1.1 - reset user credentials
        TactSharedPrefController.resetCredentials();

        //2 - Change Sync Status to NotSync
        TactSharedPrefController.setNoSynced();

        //3 - Clear initial download URL
        TactSharedPrefController.setInitialDownloadURL("");

        //Remove Onboarding preferences if they have not been removed
        TactSharedPrefController.cleanOnboardingSharedPreferences();

        //Remove Calendar Ids
        TactSharedPrefController.removeOnboardingCalendarIds();

        //4 - Remove persisted data (TODO)

        //5 - stop the CUDL sync service
        context.stopService(new Intent(context, TactCUDLSyncService.class));

        TactApplication.getInstance().clearFragmentBackStack();

        System.gc();

        //5 - Start Landing Activity
        context.startActivity(new Intent(context, LandingActivity.class));


    }

    public static void addOnboardingSourceId(EventAddSourceSuccess eventAddSourceSuccess){
        JSONArray jsonArray = eventAddSourceSuccess.response;
        for (int i = 0; i < jsonArray.length(); i ++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String source = jsonObject.getString("_type");
                Integer source_id = jsonObject.getInt("_id");
                EventBus.getDefault().post(new EventAddSourceOnboarding(source, source_id));
            }
            catch (Exception e){}
        }
    }

    public static String getEntityAsString(HttpEntity entity) {
        String jsonString = null;
        Header encoding = entity.getContentEncoding();

        try {

            if (encoding != null && encoding.getValue().equals("gzip")) {
                //              if (_logLevel.debug()) Log.d(_TAG, "Content Encoding: [gzip]");
                GZIPInputStream gzipInputStream;

                gzipInputStream = new GZIPInputStream(entity.getContent());

                BufferedReader reader = new BufferedReader( new InputStreamReader( gzipInputStream ), 12*1024) ;
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ( (line = reader.readLine()) != null) {
                    sb.append(line);
                }
                jsonString = sb.toString();
                sb = null;
            }
            else {
                //            if (_logLevel.debug()) Log.d(_TAG, "Content Encoding: [none]");
                jsonString = EntityUtils.toString(entity);
            }

        } catch (IllegalStateException e) {
            Log.d("IllegalStateException", "IllegalStateException: Entity is likely not repeatable and has already been read");
            jsonString = "";
        } catch (IOException e) {
            Log.d("IOException", "IOException: Data source cannot be read.");
            jsonString = "";
        }

        return jsonString;
    }

    public static boolean hasValidInitials(String f, String l) {
        return (f != null && l != null &&
                f.matches("[a-zA-Z]") && l.matches("[a-zA-Z]"));
    }
}
