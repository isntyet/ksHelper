package com.ks.dblab.kshelper.map;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.activity.BaseActivity;
import com.ks.dblab.kshelper.app.Config;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojo on 2016-05-29.
 */
public class MapActivity extends BaseActivity {

    private ViewGroup mapViewContainer;
    private MapView mapView;
    private FloatingActionButton btnSearch;
    private List<MapData> mapList = null;
    private List<MapPOIItem> poiItems = null;

    @Override
    protected void createActivity(Bundle savedInstanceState) {
        final View view = this.setContainerView(R.layout.activity_map);
        getSupportActionBar().setTitle("교내 지도");

        mapList = setMapList();

        btnSearch = (FloatingActionButton) view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);

        //마시멜로우 이상 버전 권한 체크
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(MapActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                mapView = new MapView(MapActivity.this);
                mapView.setDaumMapApiKey(Config.DAUM_MAP_API_KEY);

                mapViewContainer = (ViewGroup) view.findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);

                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.139773, 129.098502), true);
                mapView.setZoomLevel(0, true);

                //마커 추가
                setMarkerList();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MapActivity.this, "권한이 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("만약 권한을 거부하시게 되면 해당 서비스를 사용하실 수 없습니다.\n(해당 권한은 지도를 불러오기 위해 필요한 권한입니다.)\n권한을 허용 하시려면 [설정] > [권한] 에서 설정해 주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

    }

    public void setMarkerList() {

        poiItems = new ArrayList<MapPOIItem>();

        for(int i=0; i < mapList.size(); i++){
            poiItems.add(makeMaker(i, mapList.get(i).getName(), mapList.get(i).getX(), mapList.get(i).getY()));
        }

        for (int i = 0; i < poiItems.size(); i++) {
            mapView.addPOIItem(poiItems.get(i));
        }
    }

    private MapPOIItem makeMaker(int tag, String name, double x, double y) {

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(name);
        marker.setTag(tag);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(x, y));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        return marker;
    }

    private void loadDialog(List<MapData> list) {

        DialogAdapter adapter = new DialogAdapter(this, list);

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Toast.makeText(MapActivity.this, poiItems.get(position).getItemName(), Toast.LENGTH_LONG).show();
                        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mapList.get(position).getX(), mapList.get(position).getY()), 0, true);
                        mapView.selectPOIItem(poiItems.get(position), true);
                        dialog.dismiss();
                    }
                })
                .setGravity(Gravity.CENTER)
                .setMargin(50, 400, 50, 20)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }


    private List<MapData> setMapList() {
        List<MapData> mapDatas = new ArrayList<MapData>();

        mapDatas.add(makeMapData(0, "1호관 한성관", 35.1380040, 129.0984740));
        mapDatas.add(makeMapData(1, "2호관 자연관", 35.1390320, 129.0992520));
        mapDatas.add(makeMapData(2, "3호관 예술관", 35.1375600, 129.0985020));
        mapDatas.add(makeMapData(3, "4호관 상학관", 35.1376710, 129.0968630));
        mapDatas.add(makeMapData(4, "5호관 사회관", 35.1391990, 129.0967520));
        mapDatas.add(makeMapData(5, "6호관 인문관", 35.1417260, 129.0982520));
        mapDatas.add(makeMapData(6, "7호관 제1공학관", 35.1431980, 129.0965020));
        mapDatas.add(makeMapData(7, "8호관 제2공학관", 35.1431430, 129.0951680));
        mapDatas.add(makeMapData(8, "9호관 약/과학관", 35.1426150, 129.0969180));
        mapDatas.add(makeMapData(9, "10호관 강의동", 35.1378291, 129.0961187));
        mapDatas.add(makeMapData(10, "11호관 신학관", 35.1378930, 129.0956960));
        mapDatas.add(makeMapData(11, "12호관 멀티미디어관", 35.1389760, 129.0976960));
        mapDatas.add(makeMapData(12, "22호관 박물관/문화관", 35.1384770, 129.0981680));
        mapDatas.add(makeMapData(13, "23호관 제1학생회관", 35.1384210, 129.1001130));
        mapDatas.add(makeMapData(14, "24호관 제2학생회관", 35.1380320, 129.1000570));
        mapDatas.add(makeMapData(15, "25호관 용무관", 35.1392540, 129.0992240));
        mapDatas.add(makeMapData(16, "26호관 본관", 35.1385039, 129.0990979));
        mapDatas.add(makeMapData(17, "27호관 중앙도서관", 35.1390880, 129.1007240));
        mapDatas.add(makeMapData(18, "28호관 제1누리생활관", 35.138510, 129.095758));
        mapDatas.add(makeMapData(19, "29호관 제2누리생활관", 35.138621, 129.096621));
        mapDatas.add(makeMapData(20, "30호관 건학기념관", 35.139773, 129.098502));

        return mapDatas;
    }

    private MapData makeMapData(int num, String name, double x, double y) {
        MapData md = new MapData(num, name, x, y);

        return md;
    }

    @Override
    protected void destroyActivity() {

    }

    @Override
    protected void viewClick(View view) {
        if (view.getId() == R.id.btn_back) {
            this.finish();
        } else if (view.getId() == btnSearch.getId()) {
            loadDialog(mapList);
        }
    }
}
