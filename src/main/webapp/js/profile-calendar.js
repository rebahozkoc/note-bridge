let date = new Date();
let year = date.getFullYear();
let month = date.getMonth();
let today = date.getDate();

const day = document.querySelector(".calendar-dates");
const currdate = document.querySelector(".calendar-current-date");
const monthNameElem = document.querySelector(".month-name");
const prenexIcons = document.querySelectorAll(".calendar-navigation span");
const scheduleContent = document.getElementById("schedule-content");
const selectedDateElem = document.getElementById("selected-date");

const months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];


// Function to generate the calendar
const manipulate = () => {
    // Get the first day of the month
    let dayone = new Date(year, month, 1).getDay();
    // Get the last date of the month
    let lastdate = new Date(year, month + 1, 0).getDate();
    // Get the day of the last date of the month
    let dayend = new Date(year, month, lastdate).getDay();
    // Get the last date of the previous month
    let monthlastdate = new Date(year, month, 0).getDate();
    // Variable to store the generated calendar HTML
    let lit = "";
    // Loop to add the last dates of the previous month
    for (let i = dayone; i > 0; i--) {
        lit += `<li class="inactive">${monthlastdate - i + 1}</li>`;
    }
    // Loop to add the dates of the current month
    for (let i = 1; i <= lastdate; i++) {
        // Check if the current date is today
        let isToday = i === date.getDate() && month === date.getMonth() && year === date.getFullYear() ? "active" : "";
        lit += `<li class="date-item ${isToday}" onclick="updateDailySchedule(this, ${i})">${i}</li>`;
    }
    // Loop to add the first dates of the next month
    for (let i = dayend; i < 6; i++) {
        lit += `<li class="inactive">${i - dayend + 1}</li>`
    }
    // update the HTML of the dates element with the generated calendar
    day.innerHTML = lit;
    monthNameElem.innerText = `${monthNames[month]} ${year}`;

    const todayElement = document.querySelector('.date-item.active');
    if (todayElement) {
        updateDailySchedule(todayElement, today);
    }

}

// Function to update the daily schedule with the selected date
const updateDailySchedule = (element, selectedDate) => {
    // Remove active class from all dates
    const allDates = document.querySelectorAll('.date-item');
    allDates.forEach(date => {
        date.classList.remove('active');
    });

    // Add active class to the clicked date
    element.classList.add('active');

    // Format the selected date as mm/dd/yyyy
    const formattedDate = `${month < 9 ? '0' + (month + 1) : month + 1}/${selectedDate < 10 ? '0' + selectedDate : selectedDate}/${year}`;

    // Update the selected date in the title
    selectedDateElem.innerText = formattedDate;

    // Update the schedule content with the activities
    scheduleContent.innerHTML = `
        <ul class="schedule-daily">
            <li>Jam session</li>
            <li>Lecture</li>
        </ul>`;
}

manipulate();

prenexIcons.forEach(icon => {
    icon.addEventListener("click", () => {
        month = icon.id === "calendar-prev" ? month - 1 : month + 1;
        if (month < 0 || month > 11) {
            date = new Date(year, month, new Date().getDate());
            year = date.getFullYear();
            month = date.getMonth();
        } else {
            date = new Date();
        }
        manipulate();
    });
});