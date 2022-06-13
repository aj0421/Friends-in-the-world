package com.example.friends_in_the_world.Controllers;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.friends_in_the_world.Classes.Group;
import com.example.friends_in_the_world.Classes.Member;
import com.example.friends_in_the_world.Classes.Message;
import com.example.friends_in_the_world.Network.NetworkService;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController extends Application {
    private static final String SERVER_IP = "195.178.227.53";
    private static final String SERVER_PORT = "7117";

    private NetworkService netService;
    private NetConnection connection;

    private MutableLiveData<Map<String, Group>> groups;
    private MutableLiveData<Group> currentGroup;
    private MutableLiveData<Member> user;

    public MainController() {
        groups = new MutableLiveData<>();
        groups.setValue(new HashMap<>());
        currentGroup = new MutableLiveData<>();
        currentGroup.setValue(null);
        user = new MutableLiveData<>();
        user.setValue(new Member(""));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Start the network service
        Intent intent = new Intent(getBaseContext(), NetworkService.class);
        intent.putExtra(NetworkService.SERVER_IP, SERVER_IP);
        intent.putExtra(NetworkService.SERVER_PORT, SERVER_PORT);
        startService(intent);

        // Connect to the network service
        connection = new NetConnection();
        if(!bindService(intent, connection, 0)){
            Log.d("No binding", "TCPC");
        }
    }

    public class NetConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder){
            // Connect to the server
            NetworkService.LocalService ls = (NetworkService.LocalService) binder;
            netService = ls.getService();
            connect(netService);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
        }
    }

    public void send(String message) {
        netService.sendMessage(message);
    }

    public void connect(NetworkService netServ) {
        netServ.connect();
        this.netService = netServ;
    }


    public LiveData<Map<String, Group>> getGroups() {
        return groups;
    }

    public LiveData<Group> getCurrentGroup() {
        return currentGroup;
    }

    public LiveData<Member> getUser() {
        return user;
    }

    public void setUsername(String name) {
        Member u = user.getValue();
        if (u != null) {
            u.setName(name);
            user.postValue(u);
        }
    }

    private void register(String group, String id) {
        Map<String, Group> groups = this.groups.getValue();
        Group newGroup = new Group(group, id, new ArrayList<>());
        if (groups != null) {
            groups.put(group, newGroup);
            this.groups.postValue(groups);
        }
        currentGroup.postValue(newGroup);
        send(Message.members(group));
    }

    private void unregister(String id) {
        Group curGroup = this.currentGroup.getValue();
        if (curGroup != null && id.equals(curGroup.getID())) {
            this.currentGroup.postValue(null);
        }
    }

    private void members(String group, List<Member> members) {
        Group newGroup  = new Group(group, members);
        Group curGroup = this.currentGroup.getValue();
        if (curGroup != null && group.equals(curGroup.getName())) {
            newGroup.setID(curGroup.getID());
            this.currentGroup.postValue(newGroup);
        }
        Map<String, Group> groups = this.groups.getValue();
        if (groups != null) {
            groups.put(group, newGroup);
            this.groups.postValue(groups);
        }
    }

    private void groups(Map<String, Group> groups) {
        this.groups.postValue(groups);
    }

    public void location(String id, LatLng coords) {
        Member u = user.getValue();
        if (u != null) {
            u.setCoordinates(coords);
            user.postValue(u);
        }
    }

    private void exception(String message) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    public void process(String message) {
        Thread t = new ProcessMessage(message);
        t.start();
    }


    public class ProcessMessage extends Thread {
        private String message;

        private Member readMember(JSONObject obj) throws JSONException {
            String name = obj.getString("member");
            LatLng coords = null;
            try {
                String lat = obj.getString("latitude");
                String lng = obj.getString("longitude");
                if (!"NaN".equals(lat) && !"NaN".equals(lng)) {
                    coords = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                }
            } catch (JSONException e) {
                // No error for missing coordinates
            }
            return new Member(name, coords);
        }

        private List<Member> readMembers(JSONArray list) throws JSONException {
            List<Member> members = new ArrayList<>();
            for (int i = 0; i < list.length(); ++i) {
                members.add(readMember(list.getJSONObject(i)));
            }
            return members;
        }

        private Map<String, Group> readGroups(JSONArray list) throws JSONException {
            Map<String, Group> groups = new HashMap<>();
            for (int i = 0; i < list.length(); ++i) {
                String name = list.getJSONObject(i).getString("group");
                groups.put(name, new Group(name));
            }
            return groups;
        }

        private void process(JSONObject message) throws JSONException {
            String type = message.getString("type");

            String group, id;
            switch (type) {
                case "register":
                    group = message.getString("group");
                    id = message.getString("id");
                    register(group, id);
                    break;
                case "unregister":
                    id = message.getString("id");
                    unregister(id);
                    break;
                case "members":
                    group = message.getString("group");
                    List<Member> members = readMembers(message.getJSONArray("members"));
                    members(group, members);
                    break;
                case "groups":
                    Map<String, Group> groups = readGroups(message.getJSONArray("groups"));
                    groups(groups);
                    break;
                case "location":
                    id = message.getString("id");
                    String lat = message.getString("latitude");
                    String lng = message.getString("longitude");
                    LatLng coords = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    location(id, coords);
                    break;
                case "locations":
                    group = message.getString("group");
                    List<Member> locations = readMembers(message.getJSONArray("location"));
                    members(group, locations);
                    break;
                case "exception":
                    String info = message.getString("message");
                    exception(info);
                    break;
            }
        }

        ProcessMessage(String message) {
            this.message = message;
        }

        public void run() {
            try {
                JSONObject msg = new JSONObject(message);
                process(msg);
            } catch (JSONException e) {

            }
        }
    }
}
