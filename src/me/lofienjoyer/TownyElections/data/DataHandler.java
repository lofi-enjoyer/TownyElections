package me.lofienjoyer.TownyElections.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataHandler {
	
	private static Gson gson;
	private File _dataFile;
	private boolean _dataIsLoaded;
	private JSONObject _jsonData;
	private JSONObject _jsonDataToSave;
	
	/**
	 * @param dataFolder Folder where the "data.json" will be loaded/stored
	 */
	public DataHandler(File dataFolder, String fileName) {
		
		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		_jsonDataToSave = new JSONObject();
		if (!dataFolder.exists()) return;
		_dataFile = new File(dataFolder, fileName);
		_dataIsLoaded = _dataFile.exists();

		if (!_dataIsLoaded) return;
		
		JSONParser parser = new JSONParser();

		Object obj;
		try {
			obj = parser.parse(new FileReader(_dataFile));
			_jsonData = (JSONObject) obj;
		} catch (IOException | ParseException e) { e.printStackTrace(); }
		
	}
	
	/**
	 * @param dataTag Tag of the JSONObject list you want to retrieve from the data file
	 * @return JSONObject list under the tag specified
	 */
	public List<JSONObject> getDataList(String dataTag) {
		if (!_dataIsLoaded) return null;
		if (!_jsonData.containsKey(dataTag)) return null;
		JSONArray jsonArray = (JSONArray) _jsonData.get(dataTag);
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArray.size(); i++) {
			dataList.add((JSONObject) jsonArray.get(i));
		}
		return dataList;
	}
	
	/**
	 * @param dataTag Tag under which store the data list
	 * @param dataArray List of JSONObject to store
	 */
	@SuppressWarnings("unchecked")
	public void addDataList(String dataTag, List<JSONObject> dataArray) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < dataArray.size(); i++) {
			jsonArray.add(dataArray.get(i));
		}
		if (_jsonDataToSave.containsKey(dataTag)) _jsonDataToSave.remove(dataTag);
		_jsonDataToSave.put(dataTag, jsonArray);
	}
	
	/**
	 * Save data to data.json file
	 */
	public void saveData() {
		try {
			FileWriter fw = new FileWriter(_dataFile);
			fw.write(_jsonDataToSave.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Gson getGson() {
		return gson;
	}

}
