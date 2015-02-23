package com.tactile.tact.utils;

import com.tactile.tact.activities.TactApplication;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

public class LocalStorage {
//    private static LocalStorage instance = null;
//
//    private String user = null;
//    private String password = null;
//    private String uuid = null;
//    private Boolean gmail = null;
//    private Boolean exchange = null;
//    private Boolean calendar = true;
//    private Boolean contacts = true;
//    private String local_storage_file_name = "local_storage.json";
//    private Boolean runningBackGround = null;
//    private Boolean loguedInSalesforce = false;
//
//
//    /**
//     * Load data from file if exists
//     */
//    protected LocalStorage(){
//        if (!Utils.existsFile(local_storage_file_name)){
//            try {
//                Utils.createFile(local_storage_file_name, "");
//            }
//            catch (Exception e){
//                Utils.Log(e.getMessage());
//            }
//        }
//        JSONObject local_data = loadStorage();
//        if (local_data != null && local_data.length() > 0){
//            try {
//                user                = !local_data.getString("user").equals("null") ? local_data.getString("user") : null;
//                password            = !local_data.getString("password").equals("null") ? local_data.getString("password") : null;
//                uuid                = !local_data.getString("uuid").equals("null") ? local_data.getString("uuid") : null;
//                gmail               = local_data.getString("gmail").equals("true");
//                exchange            = local_data.getString("exchange").equals("true");
//                calendar            = local_data.getString("calendar").equals("true");
//                contacts            = local_data.getString("contacts").equals("true");
//                runningBackGround   = local_data.getString("runningBackGround").equals("true");
//                loguedInSalesforce  = local_data.getString("loguedInSalesforce").equals("true");
//            }
//            catch (Exception e){
//                Utils.Log(e.getMessage());
//            }
//        }
//    }
//
//    /**
//     *  Get the Local Storage instance
//     * @return the local storage instance (singleton)
//     */
//    public static synchronized LocalStorage getInstance(){
//        if (instance == null){
//            instance = new LocalStorage();
//        }
//        return instance;
//    }
//
//    /**
//     * Store the user name
//     * @param user the user name
//     */
//    public void setUser(String user){
//        this.user = user;
//        updateStorage();
//    }
//
//    /**
//     * Store the user password
//     * @param password the user registration password
//     */
//    public void setPassword(String password){
//        this.password = password;
//        updateStorage();
//    }
//
//    /**
//     * Store the user uuid
//     * @param uuid the user uuid
//     */
//    public void setUuid(String uuid){
//        this.uuid = uuid;
//        updateStorage();
//    }
//
//    /**
//     * Store if the user selected to obtain gmail contacts
//     * @param gmail
//     */
//    public void setGmail(Boolean gmail){
//        this.gmail = gmail;
//        updateStorage();
//    }
//
//    /**
//     * Store if the user selected to obtain exchange contacts
//     * @param exchange
//     */
//    public void setExchange(Boolean exchange){
//        this.exchange = exchange;
//        updateStorage();
//    }
//
//    /**
//     * Store if the user selected to obtain calendar info
//     * @param calendar
//     */
//    public void setCalendar(Boolean calendar){
//        this.calendar = calendar;
//        updateStorage();
//    }
//
//    /**
//     * Store if the user selected to obtain device contacts
//     * @param contacts
//     */
//    public void setContacts(Boolean contacts){
//        this.contacts = contacts;
//        updateStorage();
//    }
//
//    /**
//     * Store if there is some work running in background
//     * @param runningBackGround
//     */
//    public void setRunningBackGround(Boolean runningBackGround){
//        this.runningBackGround = runningBackGround;
//    }
//
//    /**
//     * Store if the user is logued in Salesforce
//     * @param loguedInSalesforce
//     */
//    public void setLoguedInSalesforce(Boolean loguedInSalesforce){
//        this.loguedInSalesforce = loguedInSalesforce;
//    }
//
//    /**
//     * Get the user name
//     * @return the user name
//     */
//    public String getUser(){
//        return user;
//    }
//
//    /**
//     * Get the user registration password
//     * @return the password
//     */
//    public String getPassword(){
//        return password;
//    }
//
//    /**
//     * Get the user uuid
//     * @return the user uuid
//     */
//    public String getUuid(){
//        return uuid;
//    }
//
//    /**
//     * Get if the user choose to obtain gmail contacts
//     * @return true/false
//     */
//    public Boolean getGmail(){
//        return gmail;
//    }
//
//    /**
//     * Get if the user choose to obtain exchange contacts
//     * @return true/false
//     */
//    public Boolean getExchange(){
//        return exchange;
//    }
//
//    /**
//     * Get if the user choose to obtain calendar info
//     * @return true/false
//     */
//    public Boolean getCalendar(){
//        return calendar;
//    }
//
//    /**
//     * Get if the user choose to obtain device contacts
//     * @return true/false
//     */
//    public Boolean getContacts(){
//        return contacts;
//    }
//
//    /**
//     * Get if there is some work running in background
//     * @return true/false
//     */
//    public Boolean getRunningBackGround(){
//        return runningBackGround != null && runningBackGround;
//    }
//
//    /**
//     * Get if the user is logued in Salesforce
//     * @return true/false
//     */
//    public Boolean getLoguedInSalesforce(){
//        return loguedInSalesforce;
//    }
//
//    /**
//     * Creates the local storage file with the current state
//     */
//    private void updateStorage(){
//        JSONObject data = new JSONObject();
//        try {
//            data.put("user", user != null ? user : "null");
//            data.put("password", password != null ? password : "null");
//            data.put("uuid", uuid != null ? uuid : "null");
//            data.put("gmail", gmail == null || gmail ? "true" : "false");
//            data.put("exchange", exchange == null || exchange ? "true" : "false");
//            data.put("calendar", calendar == null || calendar ? "true" : "false");
//            data.put("contacts", contacts == null || contacts ? "true" : "false");
//            data.put("runningBackGround", runningBackGround == null || runningBackGround ? "true" : "false");
//            data.put("loguedInSalesforce", loguedInSalesforce == null || !loguedInSalesforce ? "false" : "true");
//        }
//        catch (Exception e){
//            Utils.Log(e.getMessage());
//        }
//        if (data.length() > 0){
//            try {
//                Utils.createFile(local_storage_file_name, data.toString());
//            }
//            catch (Exception e){
//                Utils.Log(e.getMessage());
//            }
//        }
//
//    }
//
//    /**
//     * Get the json located in the local storage file
//     * @return the json
//     */
//    private JSONObject loadStorage(){
//        JSONObject obj = null;
//        try {
//            obj = new JSONObject(loadJsonAsString());
//        }
//        catch (Exception e){
//            Utils.Log(e.getMessage());
//        }
//        return obj;
//    }
//
//    /**
//     * Load the string (is a json-string) of the local storage file
//     * @return the string of the file
//     */
//    private String loadJsonAsString() {
//        String json;
//        try {
//            String path = TactApplication.getAppContext().getFilesDir().getAbsolutePath() + "/";
//            StringBuilder text = new StringBuilder();
//            BufferedReader br = new BufferedReader(new FileReader(path + local_storage_file_name));
//            String line;
//            while ((line = br.readLine()) != null) {
//                text.append(line);
//            }
//            json = new String(text);
//
//        } catch (Exception e) {
//            Utils.Log(e.getMessage());
//            return null;
//        }
//        return json;
//
//    }
//
//    /**
//     * Close the local storage for release memory and allow new users to use the local storage
//     */
//    public void closeLocalStorage() {
//        instance = null;
//
//        deleteLocalStorage();
//    }
//
//    /**
//     * This method try to deletes the LocalStorage
//     * @return boolean
//     */
//    public boolean deleteLocalStorage() {
//        boolean deleted = Utils.deleteFile(local_storage_file_name);
//        if (!deleted) {
//            Utils.Log("Han error was occurred when try to delete local storage");
//        }
//        return deleted;
//    }
}
