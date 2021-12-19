window.addEventListener("DOMContentLoaded", initializeMap);

const mapboxToken =
    "pk.eyJ1IjoibWFzdGVyc2NvZGUiLCJhIjoiY2t4N2htdG5pMDIxbzJ2dDh1endpMzNibyJ9.5p1nDNBo3aX93-Lc7IdsOg";
const mapboxEndpoint = `https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=${mapboxToken}`;
const map = L.map("map");

const marker = L.marker([0, 0], { draggable: true }).addTo(map);

async function initializeMap() {
    navigator.geolocation.getCurrentPosition((e) => {
        const lat = e.coords.latitude;
        const lng = e.coords.longitude;
        map.setView([lat, lng], 10);
        L.marker([lat, lng], { draggable: true }).addTo(map);

        setMarkerPosition(lat, lng);
    });
}

const MAPBOX_OPTIONS = {
    attribution:
        'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    maxZoom: 18,
    id: "mapbox/streets-v11",
    tileSize: 512,
    zoomOffset: -1,
    accessToken: mapboxToken,
};

L.tileLayer(mapboxEndpoint, MAPBOX_OPTIONS).addTo(map);


const LOCATE_OPTIONS = {
    enableHighAccuracy: true,
    showCompass: true,
    maxZoom: 1200,
    position: "topright",
    // layer: startMarker,
    drawCircle: true,
    showPopup: true,
    markerClass: L.marker,
};

// L.control.locate(LOCATE_OPTIONS).addTo(map);

function setMarkerPosition(lat, lng) {
    marker._latlng.lat = lat;
    marker._latlng.lng = lng;

    map.flyTo([lat, lng], map._zoom);
    payload.latitude = lat;
    payload.longitude = lng;

    latitudeInput.value = lat;
    longitudeInput.value = lng;
}

//https://github.com/perliedman/leaflet-control-geocoder
L.Control.geocoder({
    placeholder: "Input your location here",
    keepOpen: true,
})
    .on("markgeocode", function (e) {
        const coords = { latlng: e.geocode.center, name: e.geocode.name };
        console.log(coords);
        const { fullAddress, city, country } = getName(coords.name);
        payload.longitude = coords.latlng.lng;
        payload.latitude = coords.latlng.lat;
        payload.city = city;
        payload.country = country;
        payload.fullAddress = fullAddress;

        document.querySelector("[name='origin']").value = fullAddress;
        latitudeInput.value = coords.latlng.lat;
        longitudeInput.value = coords.latlng.lng;
    })
    .addTo(map);

map.on("click", function (e) {
    var { lat, lng } = e.latlng;
    setMarkerPosition(lat, lng);
});
// https://gis.stackexchange.com/a/210102
map.on("locationfound", (e) => {
    const { lat, lng } = e.latlng;
    setMarkerPosition(lat, lng);
});

marker.on("drag", function (e) {
    const { lat, lng } = e.latlng;
    setMarkerPosition(lat, lng);
});

function getName(label) {
    const parts = label.split(",");
    const fullAddress = label;
    let city = parts[0] + parts[1];
    const country = parts[parts.length - 1];
    return { fullAddress, city, country };
}
