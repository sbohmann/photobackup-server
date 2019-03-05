const jsJoda = JSJoda

const numPhotos = 250

let date = null
let assets
let infoLabel
let assetList
let index = 0
let reverse = false

window.onload = () => {
    setup()
}

function setup() {
    if (dateArgument !== 'any') {
        let yearAndMonth = dateArgument.match(/(\d{4})-(\d{2})/)
        if (yearAndMonth != null) {
            date = yearAndMonth[0]
        } else {
            date = jsJoda.LocalDate.parse(dateArgument)
        }
    }
    document.getElementById('date-paragraph').textContent = date ? date : "all dates"
    infoLabel = document.getElementById('info-paragraph')
    infoLabel.textContent = 'Requesting image list...'
    assetList = document.getElementById('assets')
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
    assets = JSON.parse(response)
    document.getElementById('start').onclick = start
    document.getElementById('back').onclick = back
    document.getElementById('forward').onclick = forward
    document.getElementById('end').onclick = end
    document.getElementById('bottom-start').onclick = start
    document.getElementById('bottom-back').onclick = back
    document.getElementById('bottom-forward').onclick = forward
    document.getElementById('bottom-end').onclick = end
    showAssets();
}

function start() {
    if (index !== 0) {
        index = 0
        showAssets()
    }
}

function back() {
    if (index > 0) {
        index -= numPhotos
        showAssets()
    }
}

function forward() {
    if (index < assets.length - numPhotos) {
        index += numPhotos
        showAssets()
    }
}

function end() {
    let lastIndex = Math.trunc((assets.length - 1) / numPhotos) * numPhotos
    if (index !== lastIndex) {
        index = lastIndex
        showAssets()
    }
}

function showAssets() {
    removeOldAssets();
    buildAssetList();
}

function removeOldAssets() {
    while (assetList.hasChildNodes()) {
        assetList.removeChild(assetList.lastChild)
    }
}

function buildAssetList() {
    infoLabel.textContent = numPhotos + (reverse ? ' oldest' : ' most recent') + ' photos from index ' + index
    let start = -index - numPhotos
    let end = assets.length - index
    console.log(start + ' - ' + end)
    for (let asset of assets.slice(start, end).reverse()) {
        appendAsset(asset, assetList);
    }
}

function appendAsset(asset, node) {
    let div = document.createElement("div")
    div.classList.add('asset')
    let header = document.createElement('h3')
    let creationDate
    try {
        creationDate = jsJoda.Instant.ofEpochMilli(asset.creationDateMs)
    } catch (error) {
        console.log(error)
        creationDate = 'Creation date (ms) out of range [' + asset.creationDateMs + ']'
    }
    header.appendChild(document.createTextNode(creationDate))
    div.appendChild(header)
    for (let resource of asset.resourceDescriptions) {
        createThumbnailImage(resource, div)
        createLink(resource, div, resource.name, '/photos/' + resource.checksum + '/' + resource.name)
        let convertedResourceName = jpegResourceName(resource.name)
        createConvertedLink(resource, div, convertedResourceName);
    }
    node.appendChild(div)
}

function createThumbnailImage(resource, div) {
    if (isNonImageResource(resource)) {
        return
    }
    let img = document.createElement('img')
    img.src = '/photos/' + resource.checksum + '/thumbnail/' + thumbnailName(resource.name)
    div.appendChild(img)
}

function isNonImageResource(resource) {
    return resource.name.match(/.*\.(mov|plist|mp4)/i);
}

function thumbnailName(originalResourceName) {
    let nameWithoutImageExtension = originalResourceName.replace(/\.(heic|png|tiff|jpg|jpeg|gif)$/ig, '')
    return nameWithoutImageExtension + '_thumbnail.jpg'
}

function createLink(resource, div, name, address) {
    let p = document.createElement('p')
    let link = document.createElement('a')
    link.href = address
    link.appendChild(document.createTextNode(name))
    p.append(link)
    div.append(p)
}

function createConvertedLink(resource, div, convertedResourceName) {
    if (!isNonImageResource(resource)) {
        createLink(resource, div, convertedResourceName + " (converted)",
            '/photos/' + resource.checksum + '/converted/' + convertedResourceName)
    }
}

function jpegResourceName(originalResourceName) {
    let result = originalResourceName.replace(/\.(heic|png|tiff|jpg|jpeg|gif)$/ig, '.jpg')
    if (!result.endsWith('.jpg')) {
        result = result + '.jpg'
    }
    return result
}
