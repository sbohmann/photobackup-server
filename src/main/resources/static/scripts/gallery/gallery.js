
const jsJoda = JSJoda
let date = null

window.onload = () => { setup() }

function setup() {
    if (dateArgument !== 'any') {
        date = jsJoda.LocalDate.parse(dateArgument)
    }
    document.getElementById("date-paragraph").textContent = date ? date : "all dates"
}
