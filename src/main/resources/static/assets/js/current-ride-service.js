window.addEventListener("DOMContentLoaded", initializeMap);
const  addPickupLocationBtn = document.getElementById('add-pickup-location-btn');
const pickupLocationInput = document.querySelector("[name='pickup-location']");

const payload = {
    latitude:0,
    longitude:0,
    time:''
}

const mapboxToken = "pk.eyJ1IjoibWFzdGVyc2NvZGUiLCJhIjoiY2t4N2htdG5pMDIxbzJ2dDh1endpMzNibyJ9.5p1nDNBo3aX93-Lc7IdsOg";
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
        getPickupLocations();
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


function setMarkerPosition(lat, lng) {
    marker._latlng.lat = lat;
    marker._latlng.lng = lng;

    map.flyTo([lat, lng], map._zoom);
    payload.latitude = lat;
    payload.longitude = lng;

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

        latitudeInput.value = coords.latlng.lat;
        longitudeInput.value = coords.latlng.lng;
    })
    .addTo(map);



function getName(label) {
    const parts = label.split(",");
    const fullAddress = label;
    let city = parts[0] + parts[1];
    const country = parts[parts.length - 1];
    return { fullAddress, city, country };
}

addPickupLocationBtn.addEventListener('click', addNewPickupLocation);


function addNewPickupLocation() {
    if (pickupLocationInput.value == '') {
        alert('Pickup location not defined');
        return;
    }
    axios.post(`/x-ride/pickup-location`, {city: pickupLocationInput.value})
        .then(response => {
            console.log(response)
            const {data, message, error} = response.data;
            alert(message);
            // setSuccessModal(message);
            getPickupLocations();
        })
        .catch(error => {
            // setErrorModal(error.response.data)
            console.log(error);
        });
}


function getPickupLocations(){
    axios.get(`/x-ride/pickup-location`)
        .then(response=>{
            const locations = response.data.data;
            displayPickupMarkers(locations);
        })
        .catch(error=>{
            console.log(error);
            // setErrorModal(error.response.data)
        })
}

function displayPickupMarkers(locations){
    if (locations.length > 0){
        console.log(locations)
        locations.forEach(location => createMarker(location.latitude, location.longitude, location.city))
    }
}


function createMarker(lat, lng, city){
    const pickupMarker = new L.marker([lat.toFixed(2), lng.toFixed(2)]).addTo(map)
    pickupMarker.bindPopup(`<b>${city}</b>`).openPopup();
}