var marker, i; var markers = [];var locations = [];
function addMarkers(){
locations = [
[48.296716249999996,9.9011025,'project.png'],
[48.297353,10.894914000000002,'project.png'],
[48.366414999999996,10.899445,'project.png'],
[48.30610599999999,9.907388999999998,'project.png'],
[48.365051666666666,10.89146888888889,'project.png'],
[48.412311666666675,10.647709999999998,'project.png'],
[48.3104,10.88841,'store.png'],
[48.303529999999995,9.902825,'store.png'],
];
for (i = 0; i < locations.length; i++) {  
    marker = new google.maps.Marker({
        position: new google.maps.LatLng(locations[i][0], locations[i][1]), 
        map: map,
        visible: false 
,        icon: locations[i][2]
    }); 
markers.push(marker);
}
}