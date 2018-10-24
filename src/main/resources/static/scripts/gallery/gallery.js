
const jsJoda = JSJoda
let date = null

window.onload = () => { setup() }

function setup() {
    if (dateArgument !== 'any') {
        date = jsJoda.LocalDate.parse(dateArgument)
    }
    document.getElementById('date-paragraph').textContent = date ? date : "all dates"
    requestImageList()
}

function requestImageList() {
    let request = new XMLHttpRequest()
    request.onload = () => handleImageListResponse(request.response)
    request.open('GET', imageListUrl())
    request.send()
}

function imageListUrl() {
    let dateSuffix = date ? '/' + date : ''
    return '/gallery/imageList' + dateSuffix;
}

function handleImageListResponse(response) {
    console.log('type of response: ' + typeof response)
    console.log(response)
}
