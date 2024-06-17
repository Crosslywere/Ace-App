const timeUsed = document.getElementById("timeUsed");
const timeSlot = document.getElementById("time");
const timeAllowed = parseInt(document.getElementById("timeAllowed").innerHTML) * 60;
const form = document.querySelector("form");

function timeWriter(t) {
	var h = Math.floor(t % (60 * 60 * 24) / (60 * 60));
	var m = Math.floor(t % (60 * 60) / 60);
	var s = Math.floor(t % 60);
	return (h >= 10 ? h : '0' + h) + ':' + (m >= 10 ? m : '0' + m) + ':' + (s >= 10 ? s : '0' + s);
}

function StartTimer() {
	var t = parseFloat(timeUsed.value);
	timeSlot.innerHTML = timeWriter(timeAllowed - t);
	var interval = setInterval(() => {
		t = parseFloat(timeUsed.value);
		t += 0.1;
		var timeRemaining = timeAllowed - t;
		timeSlot.innerHTML = timeWriter(timeRemaining);
		timeUsed.setAttribute("value", t);
		if (timeUsed.value >= timeAllowed) {
			clearInterval(interval);
			form.submit();
		}
	}, 100);
}

StartTimer();