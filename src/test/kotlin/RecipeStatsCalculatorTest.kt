import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.io.File
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeStatsCalculatorTest {

    private lateinit var expectedOutput: ExpectedOutput

    @BeforeAll
    fun initAll() {
        val name = "hf_test_calculation_fixtures_SMALL.json"
        val path = File(ClassLoader.getSystemResource(name).file).absolutePath

        val customPostcodeDeliveryTime = CustomPostcodeDeliveryTime("10120", 10, 3)
        expectedOutput = RecipeStatsCalculator(customPostcodeDeliveryTime).calculateStats(path)
    }

    @Test
    fun `Count the number of unique recipe names`() {
        assert(expectedOutput.uniqueRecipeCount == 4)
    }

    @Test
    fun `Count the number of occurrences for each unique recipe name (alphabetically ordered by recipe name`() {
        val sortedRecipesCount = expectedOutput.sortedRecipesCount

        // first recipe in sorted order
        assert(sortedRecipesCount[0] == CountPerRecipe("A5 Balsamic Veggie Chops", 1))

        // it should cover all recipes
        assert(sortedRecipesCount.size == 5)

        // `Creamy Dill Chicken` has two counts
        assert(sortedRecipesCount[3] == CountPerRecipe("Creamy Dill Chicken", 2))
    }

}