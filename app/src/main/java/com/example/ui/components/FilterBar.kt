package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.SortOption

import com.example.util.isTurkishLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    filterState: FilterState,
    sortOption: SortOption,
    isGridView: Boolean,
    availableManufacturers: List<String>,
    availableCarBrands: List<String>,
    availableScales: List<String>,
    availableTags: List<String> = emptyList(),
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (FilterState) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onToggleViewMode: () -> Unit,
    onOpenScanner: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Bar Row (Tek satır, içinde 'Arama' / 'Search' yazısı)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(if (isTr) "Arama" else "Search") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = if (isTr) "Arama" else "Search")
                },
                trailingIcon = {
                    if (filterState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = if (isTr) "Temizle" else "Clear")
                        }
                    } else {
                        IconButton(onClick = onOpenScanner) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = if (isTr) "Barkod Taraması" else "Barcode Scan",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                singleLine = true,
                maxLines = 1,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_bar")
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Sort & View Toggle Buttons
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier.testTag("btn_sort_menu")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sırala",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onToggleViewMode,
                        modifier = Modifier.testTag("btn_toggle_view")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Görünüm Değiştir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.label,
                                    fontWeight = if (option == sortOption) FontWeight.Bold else FontWeight.Normal,
                                    color = if (option == sortOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onSortChange(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Classification / Custom Tags Multi-Select Combo Box
        TagMultiSelectDropdown(
            selectedTags = filterState.selectedTags,
            availableTags = availableTags,
            onTagsSelected = { newTags ->
                onFilterChange(filterState.copy(selectedTags = newTags, selectedTag = null))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Combo Box (Dropdown) Filters Row for Manufacturer, Scale, Favorites & Needs Review
        val hasActiveFilter = filterState.manufacturer != null || filterState.carBrand != null ||
                filterState.scale != null || filterState.condition != null || filterState.selectedTags.isNotEmpty() ||
                filterState.selectedTag != null || filterState.onlyFavorites || filterState.onlyNeedsReview

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favoriler Quick Filter Button
            val isFavActive = filterState.onlyFavorites
            Surface(
                onClick = {
                    onFilterChange(filterState.copy(onlyFavorites = !filterState.onlyFavorites))
                },
                shape = RoundedCornerShape(12.dp),
                color = if (isFavActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isFavActive) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier.testTag("btn_filter_favorites")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isFavActive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isTr) "Favoriler" else "Favorites",
                        tint = if (isFavActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTr) "Favoriler" else "Favorites",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isFavActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isFavActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // İnceleme Gerekenler Quick Filter Button
            val isReviewActive = filterState.onlyNeedsReview
            Surface(
                onClick = {
                    onFilterChange(filterState.copy(onlyNeedsReview = !filterState.onlyNeedsReview))
                },
                shape = RoundedCornerShape(12.dp),
                color = if (isReviewActive) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isReviewActive) BorderStroke(1.5.dp, Color(0xFFD97706)) else null,
                modifier = Modifier.testTag("btn_filter_needs_review")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = if (isTr) "İnceleme Gerekenler" else "Needs Review",
                        tint = if (isReviewActive) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTr) "İnceleme" else "Review",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isReviewActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isReviewActive) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Manufacturer Combo Box
            FilterDropdown(
                label = if (isTr) "Üretici" else "Manufacturer",
                selectedValue = filterState.manufacturer,
                options = availableManufacturers,
                onOptionSelected = { newM ->
                    onFilterChange(filterState.copy(manufacturer = newM))
                },
                modifier = Modifier.weight(1f)
            )

            // Scale Combo Box (Sorted numerically by scale denominator)
            val scaleList = (listOf("1:18", "1:24", "1:32", "1:43", "1:64") + availableScales)
                .filter { it.isNotBlank() }
                .distinct()
                .sortedBy { s ->
                    s.replace("1:", "").trim().toIntOrNull() ?: 999
                }
            FilterDropdown(
                label = if (isTr) "Ölçek" else "Scale",
                selectedValue = filterState.scale,
                options = scaleList,
                onOptionSelected = { newScale ->
                    onFilterChange(filterState.copy(scale = newScale))
                },
                modifier = Modifier.weight(1f)
            )

            if (hasActiveFilter) {
                IconButton(
                    onClick = onClearFilters,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = if (isTr) "Filtreleri Temizle" else "Clear Filters",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    selectedValue: String?,
    options: List<String>,
    onOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = if (selectedValue != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            border = if (selectedValue != null) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedValue ?: label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selectedValue != null) FontWeight.Bold else FontWeight.Medium,
                    color = if (selectedValue != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (selectedValue != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = if (isTr) "Tümü ($label)" else "All ($label)",
                        fontWeight = if (selectedValue == null) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedValue == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    onOptionSelected(null)
                    expanded = false
                }
            )
            val sortedOptions = options
                .filter { it.isNotBlank() }
                .distinct()
                .sortedWith(compareBy {
                    val scaleNum = it.replace("1:", "").trim().toIntOrNull()
                    if (scaleNum != null) {
                        String.format("%05d", scaleNum)
                    } else if (it == "Diğer" || it == "Other") {
                        "zzzz"
                    } else {
                        it.lowercase()
                    }
                })

            sortedOptions.forEach { option ->
                val isSelected = option.equals(selectedValue, ignoreCase = true)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TagMultiSelectDropdown(
    selectedTags: Set<String>,
    availableTags: List<String>,
    onTagsSelected: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTr = isTurkishLocale()
    var expanded by remember { mutableStateOf(false) }

    val presetTags = if (isTr) {
        listOf("Spor", "Klasik", "Yarış", "Supercar", "JDM", "Muscle", "Off-Road", "Custom", "Film / Dizi", "Polis / Acil", "Konsept", "Elektrikli")
    } else {
        listOf("Sport", "Classic", "Racing", "Supercar", "JDM", "Muscle", "Off-Road", "Custom", "Movie / TV", "Police / Rescue", "Concept", "Electric")
    }
    val displayTags = (presetTags + availableTags).filter { it.isNotBlank() }.distinct()

    val isAllSelected = selectedTags.isEmpty()
    val displayText = when {
        isAllSelected -> if (isTr) "Etiketler: Hepsi" else "Tags: All"
        selectedTags.size == 1 -> if (isTr) "Etiket: ${selectedTags.first()}" else "Tag: ${selectedTags.first()}"
        else -> if (isTr) "Etiketler (${selectedTags.size} seçildi)" else "Tags (${selectedTags.size} selected)"
    }

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = if (!isAllSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            border = if (!isAllSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tag_multi_select_dropdown")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = if (!isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isAllSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isAllSelected) {
                        IconButton(
                            onClick = { onTagsSelected(emptySet()) },
                            modifier = Modifier.size(24.dp).testTag("btn_reset_tags_to_all")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isTr) "Hepsi Olarak Sıfırla" else "Reset to All",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (!isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 260.dp, max = 340.dp)
                .heightIn(max = 380.dp)
        ) {
            // Option 1: Hepsi (Selected by default when selectedTags is empty)
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isAllSelected,
                            onCheckedChange = {
                                onTagsSelected(emptySet())
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isTr) "Hepsi (Tüm Etiketler)" else "All (All Tags)",
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isTr) "Tüm modeller listelenir" else "Shows all models",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                onClick = {
                    onTagsSelected(emptySet())
                },
                modifier = Modifier.testTag("tag_option_all")
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            displayTags.forEach { tag ->
                val isChecked = selectedTags.contains(tag)
                val tagColors = com.example.util.TagColorHelper.getColorSchemeForTag(tag)
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    val newSet = if (checked) {
                                        selectedTags + tag
                                    } else {
                                        selectedTags - tag
                                    }
                                    onTagsSelected(newSet)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = tagColors.contentColor
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = tagColors.containerColor,
                                border = BorderStroke(1.dp, tagColors.borderColor)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = tagColors.contentColor,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                                        color = tagColors.contentColor
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        val newSet = if (isChecked) {
                            selectedTags - tag
                        } else {
                            selectedTags + tag
                        }
                        onTagsSelected(newSet)
                    },
                    modifier = Modifier.testTag("tag_option_$tag")
                )
            }
        }
    }
}

