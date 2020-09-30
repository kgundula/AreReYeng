package za.co.gundula.app.arereyeng.model

/**
 * Created by kgundula on 2017/02/21.
 */
class BusLine {
    lateinit var id: String
    lateinit var href: String
    lateinit var shortName: String
    lateinit var name: String
    lateinit var agency: Agency
    lateinit var mode: String
    lateinit var colour: String
    lateinit var textColour: String

    constructor(id: String, href: String, agency: Agency, name: String, shortName: String, mode: String, colour: String, textColour: String) {
        this.id = id
        this.href = href
        this.agency = agency
        this.name = name
        this.shortName = shortName
        this.mode = mode
        this.colour = colour
        this.textColour = textColour
    }

}