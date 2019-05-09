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
    parseDateArgument();
    document.getElementById('date-paragraph').textContent = date ? date : "all dates"
    infoLabel = document.getElementById('info-paragraph')
    infoLabel.textContent = 'Requesting image list...'
    assetList = document.getElementById('assets')
    requestImageList()
}

function parseDateArgument() {
    if (dateArgument !== 'any') {
        try {
            parseDateArgumentThrowing();
        } catch (error) {
            // TODO show error in UI
            console.log(error)
        }
    }
}

function parseDateArgumentThrowing() {
    let yearAndMonth = dateArgument.match(/(\d{4})-(\d{2})/)
    if (yearAndMonth != null) {
        date = yearAndMonth[0]
    } else {
        date = jsJoda.LocalDate.parse(dateArgument)
    }
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
        createThumbnailPlayer(resource, div)
        createLink(resource, div, resource.name, '/photos/' + resource.checksum + '/' + resource.name)
        createConvertedLink(resource, div, resource.name);
    }
    node.appendChild(div)
}

function createThumbnailImage(resource, div) {
    if (isNonImageResource(resource)) {
        return
    }
    let img = document.createElement('img')
    img.classList.add('thumbnail')
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

function createThumbnailPlayer(resource, div) {
    let match = resource.name.match(/(.*\.)(mov|mp4)/i)
    if (match == null) {
        return
    }
    let rawResourceName = match[1]
    let playButton = document.createElement('img')
    playButton.src = '/images/gallery/play_button.png'
    playButton.classList.add('play-button')
    div.appendChild(playButton)
    playButton.onclick = () => {
        let video = document.createElement('video')
        video.width = 200
        video.height = 200
        video.controls = true
        video.autoplay = true
        let mp4Source = document.createElement('source')
        mp4Source.src = '/videos/' + resource.checksum + '/' + rawResourceName + '.mp4'
        video.appendChild(mp4Source)
        let rawSource = document.createElement('source')
        rawSource.src = '/photos/' + resource.checksum + '/' + resource.name
        video.appendChild(rawSource)
        div.replaceChild(video, playButton)
    }
}

function createLink(resource, div, name, address) {
    let p = document.createElement('p')
    let link = document.createElement('a')
    link.href = address
    link.appendChild(document.createTextNode(name))
    p.append(link)
    div.append(p)
}

function createConvertedLink(resource, div, resourceName) {
    if (!isNonImageResource(resource)) {
        let convertedResourceName = jpegResourceName(resourceName)
        createLink(resource, div, convertedResourceName + " (converted)",
            '/photos/' + resource.checksum + '/converted/' + convertedResourceName)
    } else if (resource.name.match(/.*\.mov/i)) {
        let convertedResourceName = mp4ResourceName(resourceName)
        createLink(resource, div, convertedResourceName + " (converted)",
            '/videos/' + resource.checksum + '/' + convertedResourceName)
    }
}

function jpegResourceName(originalResourceName) {
    let result = originalResourceName.replace(/\.(heic|png|tiff|jpg|jpeg|gif)$/ig, '.jpg')
    if (!result.endsWith('.jpg')) {
        result = result + '.jpg'
    }
    return result
}

function mp4ResourceName(originalResourceName) {
    let result = originalResourceName.replace(/\.(mov)$/ig, '.mp4')
    if (!result.endsWith('.mp4')) {
        result = result + '.mp4'
    }
    return result
}
