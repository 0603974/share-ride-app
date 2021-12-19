const payload = {
    latitude:0,
    longitude:0,
    time:''
}
let activeRide = {}

var successModal = document.getElementById("myModal");
// var failedModal = document.getElementById("myModal1");
var canceledModal = document.getElementById("myModal2");

// Get the <span> element that closes the modal

let successStatusMessage = document.getElementById("status-message");
let failedStatusMessage = document.getElementById("status-message1");
let statusMessage = document.getElementById("status-message2");
let okay = document.getElementById("okay")

// const findRideBtn = document.getElementById("find-ride-btn");
const timeInput = document.getElementById("time");
// const endRideBtn = document.getElementById("end-ride-btn");

// document.addEventListener('DOMContentLoaded', getRidesForUser);

// findRideBtn.addEventListener('click', findRide);

map.on('locationfound', e=> {
    const {lat, lng} = e.latlng;
    payload.latitude = lat;
    payload.longitude = lng;
});

map.on('click', function(e){
    const {lat, lng} = e.latlng;
    payload.latitude = lat;
    payload.longitude = lng;

});

 function findRide(e){
    e.preventDefault();
    if (payload.longitude.length < 1 || payload.longitude.length < 1 || timeInput.value.length < 1){
        alert("Your location and time is required for this operation");
        return;
    }

    payload.time = timeInput.value;
         axios.post('http://localhost:8080/x-user-ride', payload)
             .then(response=>{
                 console.log(response)
                 const {data, message} = response.data

                 console.log(message);

                 successModal.style.display = "block";
                     successModal.querySelector('.modal-body').innerHTML = message;
                     successModal.querySelector('.close-modal').addEventListener('click', e=>{
                         successModal.style.display = 'none';
                     });
                     // activeRide = data.data;
                     getRidesForUser();
             })
             .catch(error=>setErrorModal(error.response.data) )


}

function setErrorModal(e){
    const {error, message, success} = e;
    failedModal.querySelector('#status-message1').textContent = error;
    failedModal.style.display = "block";
    failedModal.querySelector('#close-modal').addEventListener('click', e=>{
        failedModal.style.display = 'none';
    })
}

function getRidesForUser(){

    axios.get('http://localhost:8080/x-user-ride/my-rides')
        .then(response=>{
            const {isActive, rides, activeRides} = response.data.data;

            if(isActive) activeRide = activeRides[0];
            setFindRideButton(isActive);
        }).catch(error => {
        console.log(error.toString())
            setErrorModal(error.response.data)
    })
}

function cancelRide(e){
    axios.delete(`http://localhost:8080/x-ride/${activeRide.id}/cancel`)
        .then(response=>{
            getRidesForUser();
        }).catch(error=>{
            // setErrorModal(error.response.data)
        console.log(error);
    });
}

function setFindRideButton(active){
    if(active) {
        endRideBtn.style.display = 'block';
        findRideBtn.style.display = 'none';
        timeInput.style.display = 'none';
        // endRideBtn.addEventListener('click', cancelRide);
    }else{
        findRideBtn.style.display = 'block';
        timeInput.style.display = 'inline';
        // endRideBtn.style.display = 'none';

    }
}

function postReq(url, payload){
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
