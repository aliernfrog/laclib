package com.aliernfrog.laclib.enum

/**
 * Contains line types used in LAC maps.
 * @property ignoreWhenFiltering If this line type should be ignored when filtering
 */
enum class LACMapLineType(
    val ignoreWhenFiltering: Boolean = false
) {
    /**
     * Contains server name.
     */
    SERVER_NAME {
        private val startsWith = "Map Name:"
        override fun matches(line: String) = line.startsWith(startsWith)
        override fun getValue(line: String) = line.removePrefix(startsWith)
        override fun setValue(value: String, label: String?) = "$startsWith$value"
    },

    /**
     * Contains terrain type.
     */
    MAP_TYPE {
        private val startsWith = "Map Type:"
        override fun matches(line: String) = line.startsWith(startsWith)
        override fun getValue(line: String) = line.removePrefix(startsWith)
        override fun setValue(value: String, label: String?) = "$startsWith$value"
    },

    /**
     * Contains list of roles separated with a comma.
     */
    ROLES_LIST {
        private val startsWith = "Roles List:"
        override fun matches(line: String) = line.startsWith(startsWith)
        override fun getValue(line: String) = line.removePrefix(startsWith)
        override fun setValue(value: String, label: String?) = "$startsWith$value"
    },

    /**
     * Number options have numbers as value.
     */
    OPTION_NUMBER {
        override fun matches(line: String): Boolean {
            return OPTION_GENERAL.matches(line) && OPTION_GENERAL.getValue(line).toIntOrNull() != null
        }

        override fun getValue(line: String): String {
            return OPTION_GENERAL.getValue(line)
        }

        override fun getLabel(line: String): String? {
            return OPTION_GENERAL.getLabel(line)
        }
    },

    /**
     * Boolean options can have 'true' or 'false' as value.
     */
    OPTION_BOOLEAN {
        override fun matches(line: String): Boolean {
            return OPTION_GENERAL.matches(line) && OPTION_GENERAL.getValue(line).toBooleanStrictOrNull() != null
        }

        override fun getValue(line: String): String {
            return OPTION_GENERAL.getValue(line)
        }

        override fun getLabel(line: String): String? {
            return OPTION_GENERAL.getLabel(line)
        }
    },

    /**
     * Switch options can have 'enabled' or 'disabled' as value.
     */
    OPTION_SWITCH {
        private val types = listOf("enabled","disabled")
        override fun matches(line: String): Boolean {
            return OPTION_GENERAL.matches(line) && types.contains(OPTION_GENERAL.getValue(line))
        }

        override fun getValue(line: String): String {
            return OPTION_GENERAL.getValue(line)
        }

        override fun getLabel(line: String): String? {
            return OPTION_GENERAL.getLabel(line)
        }
    },

    /**
     * Objects.
     */
    OBJECT {
        override fun matches(line: String): Boolean {
            val split = line.split(":")
            return split.size >= 3 && (split.firstOrNull()?.contains("_Editor") == true)
        }

        override fun getValue(line: String) = line.split(":").firstOrNull().toString()
    },

    /**
     * Vehicles.
     */
    VEHICLE {
        override fun matches(line: String) = line.startsWith("Vehicle_")

        override fun getValue(line: String) = line
    },

    /**
     * Downloadable materials.
     */
    DOWNLOADABLE_MATERIAL {
        private val startsWith = "Downloadable_Content_Material|"
        override fun matches(line: String) = line.startsWith(startsWith)
        override fun getValue(line: String) = line.removePrefix(startsWith)
    },

    OPTION_GENERAL(ignoreWhenFiltering = true) {
        override fun matches(line: String) = line.split(": ").size == 2
        override fun getValue(line: String) = line.split(": ")[1]
        override fun setValue(value: String, label: String?) = "$label: $value"
        override fun getLabel(line: String) = line.split(": ")[0]
    },

    UNKNOWN(ignoreWhenFiltering = true) {
        override fun matches(line: String) = false
        override fun getValue(line: String) = "unknown"
    };

    /**
     * @param line line to check
     * @return true if [line] matches this line type
     */
    abstract fun matches(line: String): Boolean

    /**
     * @param line to get value of.
     * @return something useful, or [line] itself depending on type
     */
    abstract fun getValue(line: String): String

    /**
     * Sets value of option type line.
     * @return line after doing changes for setting the value
     */
    open fun setValue(value: String, label: String? = null): String { return "" }

    /**
     * Gets label of option type [line].
     * @return label of option type line
     */
    open fun getLabel(line: String): String? { return null }
}