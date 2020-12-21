class ExpectedOutputProvider(
    private val countPerRecipe: MutableMap<String, Int>,
    private val countPerPostcode: MutableMap<String, Int>
) {

    fun getExpectedOutput(): ExpectedOutput {

        return ExpectedOutput(
            uniqueRecipeCount = getUniqueRecipeCount(),
            sortedRecipesCount = getSortedRecipeCount(),
            busiestPostcode = getBusiestPostcode()
        )
    }

    // counts the number of unique recipe names
    private fun getUniqueRecipeCount(): Int {

        return this.countPerRecipe.filter { it.value == 1 }.count()
    }

    // counts the number of occurrences for each unique recipe name (alphabetically ordered by recipe name)
    private fun getSortedRecipeCount(): List<CountPerRecipe> {

        return countPerRecipe.toSortedMap().map {
            CountPerRecipe(it.key, it.value)
        }
    }

    // finds the postcode with most delivered recipes
    private fun getBusiestPostcode(): BusiestPostcode {

        val lastPair = countPerPostcode.toList().maxByOrNull { (_, value) -> value }!!
        return BusiestPostcode(lastPair.first, lastPair.second)
    }
}