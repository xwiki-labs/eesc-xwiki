package com.xwikisas.eesc.internal;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xwiki.component.annotation.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xwikisas.eesc.EESC;
import com.xwikisas.eesc.Group;
import com.xwikisas.eesc.User;

@Component
@Singleton
public class EESCImpl implements EESC {
	private Gson gson = new Gson();

	private String webserviceURL = "https://demo.monent.fr/interop";

	private int NUMBER_OF_TRY = 3;

	public void setServiceURL(String wsURL) {
		this.webserviceURL = wsURL;
	}

	private JsonElement parseHttpResponse(CloseableHttpResponse httpResponse) {
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == 200) {
			String jsonString;
			JsonElement json;
			try {
				jsonString = IOUtils.toString(httpResponse.getEntity()
						.getContent());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			try {
				json = gson.fromJson(jsonString, JsonElement.class);
			} catch (Exception e) {
				return null;
			}
			return json;
		}
		return null;
	}

	private JsonElement askForJSON(String url) {
		JsonElement json = null;
		try {
			// Try a few times before returning null
			for (int i = 0; i < NUMBER_OF_TRY; i++) {
				CloseableHttpClient httpClient = HttpClientBuilder.create()
						.build();
				HttpGet httpGet = new HttpGet(url);
				CloseableHttpResponse httpResponse;
				httpGet.setHeader("Content-type", "application/json");

				httpResponse = httpClient.execute(httpGet);
				json = parseHttpResponse(httpResponse);
				if (json != null) {
					break;
				}
				// Wait 0.5s before requesting once more
				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return json;
	}

	@Override
	public String getUID(String casID) {
		String convertCasIdToUserIdURL = String.format(
				"%s/annuaire/user/%s/login", webserviceURL, casID);
		JsonElement json;
		String userID;

		json = askForJSON(convertCasIdToUserIdURL);
		if (json == null) {
			return null;
		}
		userID = json.getAsJsonObject().get("id").getAsString();
		return userID;
	}

	@Override
	public User getUser(String userID) {
		String getUserURL = String.format("%s/annuaire/user/%s", webserviceURL,
				userID);
		JsonElement json;
		User user;
		String id, nickname, status, etabId;

		json = askForJSON(getUserURL);
		if (json == null) {
			return null;
		}
		id = json.getAsJsonObject().get("id").getAsString();
		nickname = json.getAsJsonObject().get("nickname").getAsString();
		status = json.getAsJsonObject().get("status").getAsString();
		etabId = json.getAsJsonObject().get("etabid").getAsString();
		user = new User(id, nickname, status, etabId);
		return user;
	}

	@Override
	public Group getGroup(String groupID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getUsersForGroup(String groupID) {
		String getUsersForGroupURL = String.format(
				"%s/annuaire/group/%s/users", webserviceURL, groupID);
		JsonElement json;
		List<User> userList = new ArrayList<User>();
		User user;
		String id, nickname, status, etabId;

		json = askForJSON(getUsersForGroupURL);
		if (json == null) {
			return userList;
		}
		for (JsonElement jsonUser : json.getAsJsonArray()) {
			id = jsonUser.getAsJsonObject().get("id").getAsString();
			nickname = jsonUser.getAsJsonObject().get("nickname").getAsString();
			status = jsonUser.getAsJsonObject().get("status").getAsString();
			etabId = json.getAsJsonObject().get("etabid").getAsString();
			user = new User(id, nickname, status, etabId);
			userList.add(user);
		}
		return userList;
	}

	@Override
	public List<Group> getGroupsForUser(String userID) {
		String getGroupsForUserURL = String.format(
				"%s/annuaire/user/%s/groups", webserviceURL, userID);
		JsonElement json;
		List<Group> groupList = new ArrayList<Group>();
		Group group;
		String id, name, type;

		json = askForJSON(getGroupsForUserURL);
		if (json == null) {
			return groupList;
		}
		for (JsonElement jsonGroup : json.getAsJsonArray()) {
			id = jsonGroup.getAsJsonObject().get("id").getAsString();
			name = jsonGroup.getAsJsonObject().get("nickname").getAsString();
			type = jsonGroup.getAsJsonObject().get("type").getAsString();
			group = new Group(id, name, type);
			groupList.add(group);
		}
		return groupList;
	}

	@Override
	public boolean isMember(String userID, String groupID) {
		String isMemberURL = String.format("%s/annuaire/group/%s/user/%s",
				webserviceURL, groupID, userID);
		JsonElement json;
		String id, nickname, status, etabId;

		json = askForJSON(isMemberURL);
		if (json == null) {
			return false;
		}
		id = json.getAsJsonObject().get("id").getAsString();
		nickname = json.getAsJsonObject().get("nickname").getAsString();
		status = json.getAsJsonObject().get("status").getAsString();
		etabId = json.getAsJsonObject().get("etabid").getAsString();
		try {
			new User(id, nickname, status, etabId);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isFromEtab(String userID, String etabID) {
		User user = getUser(userID);
		if (user.getEtabId().equals(etabID)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Group> getAllGroups() {
		// TODO Auto-generated method stub
		return null;
	}

}
