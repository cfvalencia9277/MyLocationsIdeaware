package com.fabian.mylocationsideaware;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.fabian.mylocationsideaware.Adapters.PlacesListAdapter;
import com.fabian.mylocationsideaware.Utils.PlaceDB;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orm.SugarContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    View saved_line;
    View places_line;
    View map;
    RecyclerView list;
    TextView placesbtn;
    TextView mapviewbtn;
    double longitud;
    double lattitude;
    View findmebtn;
    View addplacebtn;
    LocationManager lm;
    Location location;
    RecyclerView recyclerView;
    PlacesListAdapter adapter;
    Button spotmebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SugarContext.init(this);
        map = findViewById(R.id.map);
        list = (RecyclerView) findViewById(R.id.list);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findmebtn = findViewById(R.id.findmebtn);
        findmebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setmyposition();
            }
        });
        addplacebtn = findViewById(R.id.addplacebtn);
        addplacebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addplace();
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null){
            longitud = location.getLongitude();
            lattitude = location.getLatitude();
        }
        View rootview = findViewById(R.id.toolbar_main);
        spotmebtn =(Button) rootview.findViewById(R.id.spotme_btn);
        spotmebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spotme();
            }
        });
        saved_line = findViewById(R.id.save_places_bottom_line);
        places_line =  findViewById(R.id.show_places_bottom_line);
        placesbtn = (TextView) rootview.findViewById(R.id.showplacesbtn);
        placesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placesView();
            }
        });
        mapviewbtn = (TextView) rootview.findViewById(R.id.savedplacebtn);
        mapviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView();
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLocation = new LatLng(lattitude, longitud);
        mMap.addMarker(new MarkerOptions().position(currentLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }
    public void updateposition(boolean set, PlaceDB place){
        LatLng currentLocation;
        mMap.clear();
        if(!set){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null){
                longitud = location.getLongitude();
                lattitude = location.getLatitude();
            }
            currentLocation = new LatLng(lattitude, longitud);
        }else {
            currentLocation = new LatLng(place.getLat(), place.getLongitud());
        }
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(place.getNamePlace()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }
    public void mapView(){
        findmebtn.setVisibility(View.VISIBLE);
        addplacebtn.setVisibility(View.VISIBLE);
        saved_line.setBackgroundColor(getResources().getColor(R.color.spotmeback));
        places_line.setBackgroundColor(getResources().getColor(R.color.letterblue));
        placesbtn.setTextColor(getResources().getColor(R.color.letterblue));
        mapviewbtn.setTextColor(getResources().getColor(R.color.white));
        map.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
    }
    public void placesView(){
        findmebtn.setVisibility(View.GONE);
        addplacebtn.setVisibility(View.GONE);
        saved_line.setBackgroundColor(getResources().getColor(R.color.letterblue));
        places_line.setBackgroundColor(getResources().getColor(R.color.spotmeback));
        placesbtn.setTextColor(getResources().getColor(R.color.white));
        mapviewbtn.setTextColor(getResources().getColor(R.color.letterblue));
        map.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        new AsyncDBTask().execute();
    }
    public void setmyposition() {
        PlaceDB place = new PlaceDB();
        updateposition(false,place);
    }
    public void addplace() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_photo_view);
        dialog.setTitle("ADD name to this place");
        // set the custom dialog components - text, image and button
        final EditText nameET = (EditText) dialog.findViewById(R.id.text);
        final EditText citynameeET = (EditText) dialog.findViewById(R.id.text2);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogButton2 = (Button) dialog.findViewById(R.id.dialogButtonADD);
        // if button is clicked, close the custom dialog
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameET.getText().toString().isEmpty()|| citynameeET.getText().toString().isEmpty()){
                    Toast.makeText(MapsActivity.this,"Missing Data, please check",Toast.LENGTH_LONG).show();
                }else{
                    addtodb(nameET.getText().toString(),citynameeET.getText().toString(),lattitude,longitud);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    public void addtodb(String placename,String placecity,double lattitude,double longitud){
        mMap.clear();
        PlaceDB place = new PlaceDB(placename,placecity,lattitude,longitud, "none");
        place.save();
        LatLng currentLocation = new LatLng(lattitude, longitud);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(placename));
    }
    public void dialogEdit(PlaceDB place){
        final PlaceDB placediag = place;
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_place);
        dialog.setTitle(R.string.edit_place);

        // set the custom dialog components - text, image and button
        final EditText nameET = (EditText) dialog.findViewById(R.id.edittext);
        final EditText citynameeET = (EditText) dialog.findViewById(R.id.edittext2);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancelEdit);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogButton2 = (Button) dialog.findViewById(R.id.dialogButtonEdit);
        // if button is clicked, close the custom dialog
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameET.getText().toString().isEmpty()|| citynameeET.getText().toString().isEmpty()){
                    Toast.makeText(MapsActivity.this,"Missing Data, please check",Toast.LENGTH_LONG).show();
                }else{
                    Editplace(placediag,nameET.getText().toString(),citynameeET.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    public void dialogErase(PlaceDB place){
        final PlaceDB placediag = place;
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_erase);
        dialog.setTitle(R.string.erase_place);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancelErase);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogButton2 = (Button) dialog.findViewById(R.id.dialogButtonErase);
        // if button is clicked, close the custom dialog
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Eraseplace(placediag);
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void Editplace(PlaceDB place,String newname,String newcity){
        List<PlaceDB> places = PlaceDB.findWithQuery(PlaceDB.class,"SELECT * FROM PLACE_DB WHERE NAME = ?",place.getNamePlace());
        if(places.size()>0){
            PlaceDB placeedited = places.get(0);
            placeedited.setNamePlace(newname);
            placeedited.setCity(newcity);
            placeedited.save();
        }
        placesView();
    }
    public void Eraseplace(PlaceDB place){
        List<PlaceDB> places = PlaceDB.findWithQuery(PlaceDB.class,"SELECT * FROM PLACE_DB WHERE NAME = ?",place.getNamePlace());
        if(places.size()>0){
            PlaceDB placeerased = places.get(0);
            placeerased.delete();
        }
        placesView();
    }
    public void Spotme(){
        ArrayList<Float> distances = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
        Location thislocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        List<PlaceDB> places = PlaceDB.listAll(PlaceDB.class);
        for(int i=0;i<places.size();i++){
            Location testlocation = new Location("test");
            testlocation.setLatitude(places.get(i).getLat());
            testlocation.setLongitude(places.get(i).getLongitud());
            float distance = thislocation.distanceTo(testlocation);
            distances.add(distance);
        }
        int minIndex = distances.indexOf(Collections.min(distances));
        updateposition(true,places.get(minIndex));
        Toast.makeText(MapsActivity.this,"Nearest Place to you: "+places.get(minIndex).getNamePlace()+
                " at: "+distances.get(minIndex).toString()+" mt.",Toast.LENGTH_LONG).show();

    }
    public class AsyncDBTask extends AsyncTask<List<PlaceDB>,Void,List<PlaceDB>>{
        @Override
        protected List<PlaceDB> doInBackground(List<PlaceDB>... lists) {
            List<PlaceDB> placesfeed = PlaceDB.listAll(PlaceDB.class);
            return placesfeed;
        }
        @Override
        protected void onPostExecute(List<PlaceDB> placeDBs) {
            super.onPostExecute(placeDBs);
            if(placeDBs.size()>0){
                adapter = new PlacesListAdapter( placeDBs, new PlacesListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PlaceDB place) {
                        updateposition(true, place);
                        mapView();
                    }
                }, new PlacesListAdapter.OnItemClickEditListener() {
                    @Override
                    public void onItemClickEdit(PlaceDB place) {dialogEdit(place);
                    }
                }, new PlacesListAdapter.OnItemClickEraseListener() {
                    @Override
                    public void onItemClickErase(PlaceDB place) {
                        dialogErase(place);
                    }
                });
                recyclerView.setAdapter(adapter);
            }else {Toast.makeText(MapsActivity.this,"No Places Saved",Toast.LENGTH_LONG).show();}
        }
    }

}
