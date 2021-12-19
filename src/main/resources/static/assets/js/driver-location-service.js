window.addEventListener('DOMContentLoaded', initializeMap);
const longitudeInput = document.querySelector("[name='longitude']");
const latitudeInput = document.querySelector("[name='latitude']");

const mapboxToken = "pk.eyJ1IjoibWFzdGVyc2NvZGUiLCJhIjoiY2t4N2htdG5pMDIxbzJ2dDh1endpMzNibyJ9.5p1nDNBo3aX93-Lc7IdsOg";
const mapboxEndpoint = `https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=${mapboxToken}`;
const map = L.map("map");
let marker = L.marker([0, 0], { draggable: true }).addTo(map);

async function initializeMap() {
    navigator.geolocation.getCurrentPosition(e=> {
        const lat =  e.coords.latitude;
        const lng = e.coords.longitude;
        map.setView([lat, lng], 10);
        L.marker([lat, lng], { draggable: true }).addTo(map);

        // setMarkerPosition(lat, lng);
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


function createMarker(lat, lng, city){
    marker = new L.marker([lat.toFixed(2), lng.toFixed(2)]).addTo(map)
    marker.bindPopup(`<b>${city}</b>`).openPopup();
}


L.Control.geocoder({placeholder:'Input desired location here'})
    .on('markgeocode', function(e) {
        const coords = {latlng: e.geocode.center, name: e.geocode.name}
        const{fullAddress, city, country} = getName(coords.name);
        payload.longitude = coords.latlng.lng;
        payload.latitude = coords.latlng.lat;
        payload.city = city;
        payload.country = country;
        payload.fullAddress = fullAddress;
        document.querySelector("[name='origin']").value = fullAddress;
        latitudeInput.value = coords.latlng.lat;
        longitudeInput.value = coords.latlng.lng;

    }).addTo(map);



function getName(label){
    const parts = label.split(',');
    const fullAddress = label;
    let city = parts[0] + parts[1];
    const country = parts[parts.length - 1];
    return{fullAddress, city, country};
}
function setMarkerPosition(lat, lng) {
    marker._latlng.lat = lat;
    marker._latlng.lng = lng;

    map.flyTo([lat, lng], map._zoom);
    payload.latitude = lat;
    payload.longitude = lng;
}