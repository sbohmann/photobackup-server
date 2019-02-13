
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
    return '/gallery/imageList' + dateSuffix
}

function handleImageListResponse(response) {
    let assets = JSON.parse(response)
    for (let asset of assets) {
        let div = document.createElement("div")
        let header = document.createElement('h5')
        let creationDate = jsJoda.Instant. ofEpochMilli(asset.creationDateMs)
        header.appendChild(document.createTextNode(creationDate))
        div.appendChild(header)
        for (let resource of asset.resourceDescriptions) {
            let p = document.createElement("p")
            let link = document.createElement('a')
            link.href = '/photos/' + resource.checksum + '/' + resource.name
            link.appendChild(document.createTextNode(resource.name))
            p.append(link)
            div.append(p)
        }
        document.body.appendChild(div)
    }
}
