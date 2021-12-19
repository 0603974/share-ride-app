const payload = {
    latitude: 0,
    longitude: 0,
    time: '',
    city: '',
    country: '',
    fullAddress: ''
}
let activeRide = {}
let pickupLocation = {};

// const shareRideBtn = document.getElementById("share-ride-btn");
// const failedModal = document.getElementById("failedModal");
// const timeInput = document.getElementById("time");
// const successModal = document.getElementById("successModal");
// const cancelRideBtn = document.getElementById("end-ride-btn");

// document.addEventListener('DOMContentLoaded', getRidesForDriver);

// shareRideBtn.addEventListener('click', shareRide);
// showPickupLocationBox(false);

function shareRide(e) {
    e.preventDefault();
    if (payload.longitude.length < 1 || payload.longitude.length < 1 || timeInput.value.length < 1) {
        alert("Your location and time is required for this operation");
        return;
    }
    payload.time = timeInput.value;

    axios.post('http://localhost:8080/x-ride', payload)
        .then(response => {
            const {data, message, error} = response.data;
            setSuccessModal(message);
            getRidesForDriver();
        })
        .catch(error => setErrorModal(error.response.data));

}
function setSuccessModal(message){
    successModal.style.display = "block";
    successModal.querySelector('.modal-body').innerHTML = message;
    successModal.querySelector('.close-modal').addEventListener('click', e => {
        successModal.style.display = 'none';
    });
}
function setErrorModal(error) {
    failedModal.querySelector('#status-message1').textContent = error.toString();
    failedModal.style.display = "block";
    failedModal.querySelector('#close-modal').addEventListener('click', e => {
        failedModal.style.display = 'none';
    })
    console.log(error)
}

function getRidesForDriver() {

    axios.get('http://localhost:8080/x-ride/me')
        .then(response => {
            const {isActive, rides, activeRides} = response.data.data;
            if (isActive) {
                activeRide = activeRides[0];
                showPickupLocationBox(true);
                getPickupLocations();
            } else {
                showPickupLocationBox(false)
            }
            setRideButton(isActive);
        }).catch(error => {
        console.log(error);
            // setErrorModal(error.response.data);
        console.log(error.response)
    })
}

function endRide(e) {
    axios.delete(`http://localhost:8080/x-ride/${activeRide.id}/cancel`)
        .then(response => {
            getRidesForDriver();
        }).catch(error => setErrorModal(error.response.data));
}

function setRideButton(active) {
    if (active) {
        cancelRideBtn.style.display = 'block';
        shareRideBtn.style.display = 'none';
        timeInput.style.display = 'none';
        cancelRideBtn.addEventListener('click', endRide);
    } else {
        shareRideBtn.style.display = 'block';
        timeInput.style.display = 'inline';
        cancelRideBtn.style.display = 'none';

    }
}

function postReq(url, payload) {
    //https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch

    const OPTIONS = {
        method: 'POST',
        mode: 'cors',
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            'Content-Type': 'application/json'
            // 'Content-Type': 'application/x-www-form-urlencoded',
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(payload) // body data type must match "Content-Type" header
    }
    return fetch(url, OPTIONS);
}

function addNewPickupLocation() {
    const cityInput = document.getElementById('pick-up-location');
    if (cityInput.value == '') {
        alert('Pickup location not defined');
        return;
    }
    axios.post(`/x-ride/${activeRide.id}/pickup-location`, {city: cityInput.value})
        .then(response => {
            console.log(response)
            const {data, message, error} = response.data;
            setSuccessModal(message);
            getPickupLocations();
        })
        .catch(error => setErrorModal(error.response.data));
}

function setPickupLocation() {
    pickupLocation = payload;
}

function showPickupLocationBox(show) {
    if (show) {
        document.getElementById('pick-up-location-box').style.display = 'block';
        document.getElementById('add-pickup-location-btn').addEventListener('click', addNewPickupLocation);
    }
    if (!show) document.getElementById('pick-up-location-box').style.display = 'none';
}

function populatePickupFields() {
    const pickupLocationInput = document.getElementById('pick-up-address');
    // const  cityInput = document.getElementById('pick-up-city');

    pickupLocationInput.value = pickupLocation.fullAddress;
    // cityInput.value = pickupLocation.city;
}

function getPickupLocations(){
    axios.get(`/x-ride/${activeRide.id}/pickup-location`)
        .then(response=>{
            const locations = response.data.data;
            displayPickupMarkers(locations);
        })
        .catch(error=>setErrorModal(error.response.data))
}

function displayPickupMarkers(locations){
    if (locations.length > 0){
        locations.forEach(location => createMarker(location.latitude, location.longitude, location.city))
    }
}
