import com.google.gson.GsonBuilder
import java.io.File

fun main() {

    val name = "hf_test_calculation_fixtures_SMALL.json"
    val path = File(ClassLoader.getSystemResource(name).file).absolutePath

    val customPostcodeDeliveryTime = CustomPostcodeDeliveryTime(
        "10120",
        10,
        3,
    )

    val expectedOutput = RecipeStatsCalculator(
        customPostcodeDeliveryTime,
        mutableListOf("Potato", "Veggie", "Mushroom")
    ).calculateStats(path)


    val gson = GsonBuilder().setPrettyPrinting().create()
    val json = gson.toJson(expectedOutput)

    println(json)
}