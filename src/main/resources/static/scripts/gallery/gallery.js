const jsJoda = JSJoda
let date = null

window.onload = () => {
    setup()
}

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
        let creationDate;
        try {
            creationDate = jsJoda.Instant.ofEpochMilli(asset.creationDateMs)
        } catch (error) {
            console.log(error)
            creationDate = 'Creation date (ms) out of range [' + asset.creationDateMs + ']'
        }
        header.appendChild(document.createTextNode(creationDate))
        div.appendChild(header)
        for (let resource of asset.resourceDescriptions) {
            createLink(resource, div, resource.name, '/photos/' + resource.checksum + '/' + resource.name);
            let convertedResourceName = jpegResourceName(resource.name);
            createLink(resource, div, convertedResourceName + " (converted)",
                '/photos/' + resource.checksum + '/converted/' + convertedResourceName);
        }
        document.body.appendChild(div)
    }
}

function createLink(resource, div, name, address) {
    let p = document.createElement("p")
    let link = document.createElement('a')
    link.href = address
    link.appendChild(document.createTextNode(name))
    p.append(link)
    div.append(p)
}

function jpegResourceName(originalResourceName) {
    result = originalResourceName.replace('.(heic|png|tiff|jpg|jpeg|gif)', '.jpg');
    if (!result.endsWith('.jpg')) {
        result = result + '.jpg';
    }
    return result;
}
