class ExpectedOutputProvider {

    // counts the number of unique recipe names
    fun getUniqueRecipeCount(countPerRecipe: MutableMap<String, Int>): Int {

        return countPerRecipe.filter { it.value == 1 }.count()
    }

    // counts the number of occurrences for each unique recipe name (alphabetically ordered by recipe name)
    fun getSortedRecipeCount(countPerRecipe: MutableMap<String, Int>): List<CountPerRecipe> {

        return countPerRecipe.toSortedMap().map {
            CountPerRecipe(it.key, it.value)
        }
    }

    // finds the postcode with most delivered recipes
    fun getBusiestPostcode(countPerPostcode: MutableMap<String, Int>): BusiestPostcode {

        val lastPair = countPerPostcode.toList().maxByOrNull { (_, value) -> value }!!
        return BusiestPostcode(lastPair.first, lastPair.second)
    }
}