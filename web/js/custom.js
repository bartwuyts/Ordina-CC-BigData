var previousInfoWindow;

function initiateMap(city) {
    var map = new GMaps({
        el: '#map-canvas',
        zoom: 10,
        lat: 51.150000,
        lng: 4.133333,
    });

    drawSectors(map, city);
    drawSpeedings(map, city);
}

function drawSectors(map, city) {
    var url = "http://localhost:8080/map/sectors/" + city;
    $.ajax({
        dataType: "json",
        url: url,
        success: function (data) {

            data.forEach(function (entry) {
                var geometry = entry.geometry;
                var color = getRandomColor();
                
                if(geometry.type === "MultiPolygon") {
                    for(var i = 0 ; i < geometry.coordinates[0].length ; ++i) {
                        drawSector(map, entry.name, color, [geometry.coordinates[0][i]]);
                    }
                } else if (geometry.type === "Polygon") {
                    drawSector(map, entry.name, color, geometry.coordinates);
                }

            });
        }
    });
}

function drawSector(map, name, color, coordinates) {
    var polygon = map.drawPolygon({
        paths: coordinates,
        useGeoJSON: true,
        strokeOpacity: 1,
        strokeWeight: 3,
        fillColor: color,
        fillOpacity: 0.4,
        click: function (e) {
            var infoWindow = new google.maps.InfoWindow({
                content: '<p><h1>' + name + '</h1></p><p>Additional info per sector - Coming soon..?</p>'
            });
            var center = getCentroid(coordinates[0]);
            infoWindow.setPosition(new google.maps.LatLng(center.x, center.y));
            infoWindow.open(polygon.map);
            if(previousInfoWindow) {
                previousInfoWindow.close();
            }
            previousInfoWindow = infoWindow;
        }
    });
}

function getCentroid(coordinates) {
    var twicearea = 0,
        x = 0,
        y = 0,
        nCoordinates = coordinates.length,
        f;

    for (var i = 0, j = nCoordinates - 1; i < nCoordinates; j = i++) {
        var x1 = coordinates[i].lat();
        var y1 = coordinates[i].lng();

        var x2 = coordinates[j].lat();
        var y2 = coordinates[j].lng();

        f = x1 * y2 - x2 * y1;
        twicearea += f;
        x += (x1 + x2) * f;
        y += (y1 + y2) * f;
    }

    f = twicearea * 3;

    return {
        x: x / f,
        y: y / f
    };
}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function drawSpeedings(map, city) {
    var url = "http://localhost:8080/map/speedings/" + city;
    $.ajax({
        dataType: "json",
        url: url,
        success: function (data) {
            data.forEach(function (entry) {
                var geometry = entry.geometry;
                var speedColor = getSpeedingColor(entry.allowed_velocity);
                map.drawPolygon({
                    paths: geometry.coordinates,
                    useGeoJSON: true,
                    strokeColor: speedColor,
                    strokeOpacity: 1,
                    strokeWeight: 6
                });

            });
        }
    });
}

function getSpeedingColor(speed) {
    switch (speed) {
    case 30:
        return "#0F0";
    case 50:
        return "#0FF";
    case 70:
        return "#00F";
    case 90:
        return "#F00";
    default:
        return "#000";
    }
}