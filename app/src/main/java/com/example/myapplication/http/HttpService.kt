package com.example.myapplication.http

import com.example.myapplication.models.PhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Node
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class HttpService private constructor(){

    private val mainurl = "https://api.flickr.com/services/rest/"
    private val apikey = "&api_key=4a10c84dfc0ab4b04c7368f8d30c8548"
    companion object {
        private var single = HttpService()

        fun getInstance(): HttpService {
            if (single == null)
                single = HttpService()
            return single
        }
    }


    suspend fun httpGet(methodToAccess: String, searchKey: String): MutableList<PhotoModel> {
        val result = withContext(Dispatchers.IO) {
            val inputStream: InputStream
            val url: String
            // create URL
            if(searchKey == "")
            {
                url = mainurl + methodToAccess + apikey + "&per_page=20"
            }
            else
            {
                url = mainurl + methodToAccess + apikey + "&text=${searchKey}&per_page=20"
            }

            // create HttpURLConnection
            val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.connect()

            // receive response as inputStream
            inputStream = conn.inputStream

            // convert inputstream to string
            if (inputStream != null)
                convertInputStreamToString(url)
            else
                mutableListOf<PhotoModel>()
        }
        return result
    }

    private fun convertInputStreamToString(myURL: String?): MutableList<PhotoModel> {
        val photos = mutableListOf<PhotoModel>()

        val builderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = builderFactory.newDocumentBuilder()
        val doc = docBuilder.parse(myURL)
        val nList = doc.getElementsByTagName("photo")

        for(i in 0 until nList.length)
        {
            val currentItem: Node = nList.item(i)
            val id = currentItem.attributes.getNamedItem("id").nodeValue
            val owner = currentItem.attributes.getNamedItem("owner").nodeValue
            val secret = currentItem.attributes.getNamedItem("secret").nodeValue
            val server = currentItem.attributes.getNamedItem("server").nodeValue
            val farm = currentItem.attributes.getNamedItem("farm").nodeValue
            val title = currentItem.attributes.getNamedItem("title").nodeValue
            val ispublic = currentItem.attributes.getNamedItem("ispublic").nodeValue
            val isfriend = currentItem.attributes.getNamedItem("isfriend").nodeValue
            val isfamily = currentItem.attributes.getNamedItem("isfamily").nodeValue
            val img_url = "https://live.staticflickr.com/${server}/${id}_${secret}_w.jpg"
            photos.add(PhotoModel(id, owner, secret, server, farm, title, ispublic, isfriend, isfamily, img_url))
        }
        return photos
    }
}