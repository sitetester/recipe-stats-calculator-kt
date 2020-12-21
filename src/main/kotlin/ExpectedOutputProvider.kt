class ExpectedOutputProvider(private val countPerRecipe: MutableMap<String, Int>) {

    fun getExpectedOutput(): ExpectedOutput {

        return ExpectedOutput(
            uniqueRecipeCount = getUniqueRecipeCount(),
            sortedRecipesCount = getSortedRecipeCount(),
        )
    }

    private fun getUniqueRecipeCount(): Int {

        return this.countPerRecipe.filter { it.value == 1 }.count()
    }

    private fun getSortedRecipeCount(): List<CountPerRecipe> {

        return countPerRecipe.toSortedMap().map {
            CountPerRecipe(it.key, it.value)
        }
    }
}