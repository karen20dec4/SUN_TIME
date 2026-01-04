package com.android.sun.data. model

/**
 * Model pentru un oraș predefinit (pentru căutare)
 * NU se salvează în DB - e doar pentru referință
 */
data class PredefinedCity(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timeZone: Double
) {
    /**
     * Returnează numele complet pentru afișare:  "București, Romania"
     */
    fun getDisplayName(): String = "$name, $country"
    
    /**
     * Convertește în LocationData pentru salvare în DB
     */
    fun toLocationData(): LocationData = LocationData(
        id = 0, // Room va genera ID-ul
        name = "$name, $country",
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        timeZone = timeZone,
        isCurrentLocation = false
    )
}

		/**
		 * Lista completă de orașe predefinite pentru căutare
		 * ~1200 orașe:  Capitalele lumii (195) + Orașe Europa (1000+)
		 */
		object PredefinedCities {
			
				/**
			 * Caută orașe după nume (case-insensitive, partial match)
			 * ✅ Suportă căutare fără diacritice (Brasov găsește Brașov)
			 */
			fun search(query: String): List<PredefinedCity> {
				if (query.isBlank()) return emptyList()
				
				val searchTerm = removeDiacritics(query. trim().lowercase())
				
				return allCities.filter { city ->
					val cityNameNormalized = removeDiacritics(city.name.lowercase())
					val countryNormalized = removeDiacritics(city.country.lowercase())
					
					cityNameNormalized. contains(searchTerm) ||
					countryNormalized.contains(searchTerm)
				}. take(20) // Limitează la 20 rezultate pentru performanță
			}
			
			/**
			 * ✅ Elimină diacriticele din text
			 * Exemplu: "Brașov" -> "brasov", "Malmö" -> "malmo"
			 */
			private fun removeDiacritics(text:  String): String {
				val diacriticsMap = mapOf(
					'ă' to 'a', 'â' to 'a', 'á' to 'a', 'à' to 'a', 'ä' to 'a', 'å' to 'a', 'ã' to 'a',
					'î' to 'i', 'í' to 'i', 'ì' to 'i', 'ï' to 'i',
					'ș' to 's', 'ş' to 's', 'š' to 's',
					'ț' to 't', 'ţ' to 't',
					'ö' to 'o', 'ó' to 'o', 'ò' to 'o', 'ô' to 'o', 'õ' to 'o', 'ø' to 'o',
					'ü' to 'u', 'ú' to 'u', 'ù' to 'u', 'û' to 'u',
					'é' to 'e', 'è' to 'e', 'ê' to 'e', 'ë' to 'e', 'ě' to 'e',
					'ñ' to 'n', 'ń' to 'n',
					'ç' to 'c', 'č' to 'c', 'ć' to 'c',
					'ž' to 'z', 'ź' to 'z',
					'ł' to 'l',
					'ß' to 's',
					'ý' to 'y', 'ÿ' to 'y',
					'đ' to 'd', 'ð' to 'd'
				)
				
				return text. map { char -> 
					diacriticsMap[char] ?: char 
				}.joinToString("")
			}
    
    
	
	
	
	/**
     * Lista completă de orașe
     * TODO: Adaugă aici toate cele 1200 de orașe
     */
    val allCities: List<PredefinedCity> = listOf(
        // ══════════════════════════════════════════════════════════
        // CAPITALELE LUMII (195 țări)
        // ══════════════════════════════════════════════════════════
        
        // Asia
        PredefinedCity("Tokyo", "Japan", 35.6762, 139.6503, 40.0, 9.0),
        PredefinedCity("Beijing", "China", 39.9042, 116.4074, 44.0, 8.0),
        PredefinedCity("New Delhi", "India", 28.6139, 77.2090, 216.0, 5.5),
        PredefinedCity("Seoul", "South Korea", 37.5665, 126.9780, 38.0, 9.0),
        PredefinedCity("Bangkok", "Thailand", 13.7563, 100.5018, 1.5, 7.0),
        PredefinedCity("Hanoi", "Vietnam", 21.0285, 105.8542, 12.0, 7.0),
        PredefinedCity("Jakarta", "Indonesia", -6.2088, 106.8456, 8.0, 7.0),
        PredefinedCity("Manila", "Philippines", 14.5995, 120.9842, 16.0, 8.0),
        PredefinedCity("Singapore", "Singapore", 1.3521, 103.8198, 15.0, 8.0),
        PredefinedCity("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 66.0, 8.0),
        PredefinedCity("Taipei", "Taiwan", 25.0330, 121.5654, 9.0, 8.0),
        PredefinedCity("Hong Kong", "Hong Kong", 22.3193, 114.1694, 32.0, 8.0),
        PredefinedCity("Ankara", "Turkey", 39.9334, 32.8597, 938.0, 3.0),
        PredefinedCity("Tel Aviv", "Israel", 32.0853, 34.7818, 5.0, 2.0),
        PredefinedCity("Riyadh", "Saudi Arabia", 24.7136, 46.6753, 612.0, 3.0),
        PredefinedCity("Dubai", "UAE", 25.2048, 55.2708, 5.0, 4.0),
        PredefinedCity("Tehran", "Iran", 35.6892, 51.3890, 1189.0, 3.5),
        PredefinedCity("Baghdad", "Iraq", 33.3152, 44.3661, 34.0, 3.0),
        PredefinedCity("Kabul", "Afghanistan", 34.5553, 69.2075, 1791.0, 4.5),
        PredefinedCity("Islamabad", "Pakistan", 33.6844, 73.0479, 507.0, 5.0),
        PredefinedCity("Dhaka", "Bangladesh", 23.8103, 90.4125, 9.0, 6.0),
        PredefinedCity("Colombo", "Sri Lanka", 6.9271, 79.8612, 1.0, 5.5),
        PredefinedCity("Kathmandu", "Nepal", 27.7172, 85.3240, 1400.0, 5.75),
        PredefinedCity("Thimphu", "Bhutan", 27.4728, 89.6390, 2334.0, 6.0),
        PredefinedCity("Ulaanbaatar", "Mongolia", 47.8864, 106.9057, 1350.0, 8.0),
        PredefinedCity("Pyongyang", "North Korea", 39.0392, 125.7625, 38.0, 9.0),
        
        // Africa
        PredefinedCity("Cairo", "Egypt", 30.0444, 31.2357, 75.0, 2.0),
        PredefinedCity("Lagos", "Nigeria", 6.5244, 3.3792, 41.0, 1.0),
        PredefinedCity("Johannesburg", "South Africa", -26.2041, 28.0473, 1753.0, 2.0),
        PredefinedCity("Nairobi", "Kenya", -1.2921, 36.8219, 1795.0, 3.0),
        PredefinedCity("Casablanca", "Morocco", 33.5731, -7.5898, 27.0, 1.0),
        PredefinedCity("Addis Ababa", "Ethiopia", 8.9806, 38.7578, 2355.0, 3.0),
        PredefinedCity("Algiers", "Algeria", 36.7372, 3.0863, 424.0, 1.0),
        PredefinedCity("Tunis", "Tunisia", 36.8065, 10.1815, 4.0, 1.0),
        PredefinedCity("Tripoli", "Libya", 32.8872, 13.1913, 81.0, 2.0),
        PredefinedCity("Accra", "Ghana", 5.6037, -0.1870, 61.0, 0.0),
        PredefinedCity("Dakar", "Senegal", 14.7167, -17.4677, 22.0, 0.0),
        PredefinedCity("Kinshasa", "DR Congo", -4.4419, 15.2663, 240.0, 1.0),
        PredefinedCity("Luanda", "Angola", -8.8390, 13.2894, 6.0, 1.0),
        PredefinedCity("Khartoum", "Sudan", 15.5007, 32.5599, 382.0, 2.0),
        
        // America de Nord
        PredefinedCity("Washington D.C.", "USA", 38.9072, -77.0369, 125.0, -5.0),
        PredefinedCity("Ottawa", "Canada", 45.4215, -75.6972, 70.0, -5.0),
        PredefinedCity("Mexico City", "Mexico", 19.4326, -99.1332, 2240.0, -6.0),
        PredefinedCity("Havana", "Cuba", 23.1136, -82.3666, 59.0, -5.0),
        PredefinedCity("Guatemala City", "Guatemala", 14.6349, -90.5069, 1500.0, -6.0),
        PredefinedCity("Panama City", "Panama", 8.9824, -79.5199, 0.0, -5.0),
        
        // America de Sud
        PredefinedCity("Brasília", "Brazil", -15.8267, -47.9218, 1172.0, -3.0),
        PredefinedCity("Buenos Aires", "Argentina", -34.6037, -58.3816, 25.0, -3.0),
        PredefinedCity("Santiago", "Chile", -33.4489, -70.6693, 570.0, -4.0),
        PredefinedCity("Lima", "Peru", -12.0464, -77.0428, 161.0, -5.0),
        PredefinedCity("Bogotá", "Colombia", 4.7110, -74.0721, 2640.0, -5.0),
        PredefinedCity("Caracas", "Venezuela", 10.4806, -66.9036, 900.0, -4.0),
        PredefinedCity("Quito", "Ecuador", -0.1807, -78.4678, 2850.0, -5.0),
        PredefinedCity("Montevideo", "Uruguay", -34.9011, -56.1645, 43.0, -3.0),
        PredefinedCity("Asunción", "Paraguay", -25.2637, -57.5759, 43.0, -4.0),
        PredefinedCity("La Paz", "Bolivia", -16.4897, -68.1193, 3640.0, -4.0),
        
        // Oceania
        PredefinedCity("Canberra", "Australia", -35.2809, 149.1300, 578.0, 10.0),
        PredefinedCity("Wellington", "New Zealand", -41.2866, 174.7756, 0.0, 12.0),
        PredefinedCity("Suva", "Fiji", -18.1416, 178.4419, 0.0, 12.0),
        
        // ══════════════════════════════════════════════════════════
        // ORAȘE MARI DIN EUROPA 
        // ══════════════════════════════════════════════════════════
        
		// ══════════════════════════════════════════════════════════
        // GERMANIA (UTC+1) - 30 orașe
        // Berlin (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Berlin", "Germany", 52.5200, 13.4050, 34.0, 1.0),
        PredefinedCity("Hamburg", "Germany", 53.5511, 9.9937, 6.0, 1.0),
        PredefinedCity("Munich", "Germany", 48.1351, 11.5820, 519.0, 1.0),
        PredefinedCity("Cologne", "Germany", 50.9375, 6.9603, 53.0, 1.0),
        PredefinedCity("Frankfurt", "Germany", 50.1109, 8.6821, 112.0, 1.0),
        PredefinedCity("Stuttgart", "Germany", 48.7758, 9.1829, 247.0, 1.0),
        PredefinedCity("Düsseldorf", "Germany", 51.2277, 6.7735, 38.0, 1.0),
        PredefinedCity("Leipzig", "Germany", 51.3397, 12.3731, 113.0, 1.0),
        PredefinedCity("Dortmund", "Germany", 51.5136, 7.4653, 86.0, 1.0),
        PredefinedCity("Essen", "Germany", 51.4556, 7.0116, 116.0, 1.0),
        PredefinedCity("Bremen", "Germany", 53.0793, 8.8017, 11.0, 1.0),
        PredefinedCity("Dresden", "Germany", 51.0504, 13.7373, 113.0, 1.0),
        PredefinedCity("Hanover", "Germany", 52.3759, 9.7320, 55.0, 1.0),
        PredefinedCity("Nuremberg", "Germany", 49.4521, 11.0767, 309.0, 1.0),
        PredefinedCity("Duisburg", "Germany", 51.4344, 6.7623, 31.0, 1.0),
        PredefinedCity("Bochum", "Germany", 51.4818, 7.2162, 100.0, 1.0),
        PredefinedCity("Wuppertal", "Germany", 51.2562, 7.1508, 160.0, 1.0),
        PredefinedCity("Bielefeld", "Germany", 52.0302, 8.5325, 118.0, 1.0),
        PredefinedCity("Bonn", "Germany", 50.7374, 7.0982, 60.0, 1.0),
        PredefinedCity("Münster", "Germany", 51.9607, 7.6261, 60.0, 1.0),
        PredefinedCity("Karlsruhe", "Germany", 49.0069, 8.4037, 115.0, 1.0),
        PredefinedCity("Mannheim", "Germany", 49.4875, 8.4660, 97.0, 1.0),
        PredefinedCity("Augsburg", "Germany", 48.3705, 10.8978, 489.0, 1.0),
        PredefinedCity("Wiesbaden", "Germany", 50.0782, 8.2398, 115.0, 1.0),
        PredefinedCity("Mönchengladbach", "Germany", 51.1805, 6.4428, 50.0, 1.0),
        PredefinedCity("Gelsenkirchen", "Germany", 51.5177, 7.0857, 60.0, 1.0),
        PredefinedCity("Aachen", "Germany", 50.7753, 6.0839, 173.0, 1.0),
        PredefinedCity("Braunschweig", "Germany", 52.2689, 10.5268, 75.0, 1.0),
        PredefinedCity("Kiel", "Germany", 54.3233, 10.1228, 5.0, 1.0),
        PredefinedCity("Freiburg", "Germany", 47.9990, 7.8421, 278.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // FRANȚA (UTC+1) - 30 orașe
        // Paris (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Paris", "France", 48.8566, 2.3522, 35.0, 1.0),
        PredefinedCity("Marseille", "France", 43.2965, 5.3698, 12.0, 1.0),
        PredefinedCity("Lyon", "France", 45.7640, 4.8357, 173.0, 1.0),
        PredefinedCity("Toulouse", "France", 43.6047, 1.4442, 141.0, 1.0),
        PredefinedCity("Nice", "France", 43.7102, 7.2620, 10.0, 1.0),
        PredefinedCity("Nantes", "France", 47.2184, -1.5536, 8.0, 1.0),
        PredefinedCity("Strasbourg", "France", 48.5734, 7.7521, 142.0, 1.0),
        PredefinedCity("Montpellier", "France", 43.6108, 3.8767, 27.0, 1.0),
        PredefinedCity("Bordeaux", "France", 44.8378, -0.5792, 17.0, 1.0),
        PredefinedCity("Lille", "France", 50.6292, 3.0573, 21.0, 1.0),
        PredefinedCity("Rennes", "France", 48.1173, -1.6778, 30.0, 1.0),
        PredefinedCity("Reims", "France", 49.2583, 4.0317, 88.0, 1.0),
        PredefinedCity("Le Havre", "France", 49.4944, 0.1079, 27.0, 1.0),
        PredefinedCity("Saint-Étienne", "France", 45.4397, 4.3872, 500.0, 1.0),
        PredefinedCity("Toulon", "France", 43.1242, 5.9280, 10.0, 1.0),
        PredefinedCity("Grenoble", "France", 45.1885, 5.7245, 212.0, 1.0),
        PredefinedCity("Dijon", "France", 47.3220, 5.0415, 245.0, 1.0),
        PredefinedCity("Angers", "France", 47.4784, -0.5632, 20.0, 1.0),
        PredefinedCity("Nîmes", "France", 43.8367, 4.3601, 39.0, 1.0),
        PredefinedCity("Villeurbanne", "France", 45.7676, 4.8799, 170.0, 1.0),
        PredefinedCity("Clermont-Ferrand", "France", 45.7772, 3.0870, 401.0, 1.0),
        PredefinedCity("Le Mans", "France", 48.0061, 0.1996, 51.0, 1.0),
        PredefinedCity("Aix-en-Provence", "France", 43.5297, 5.4474, 177.0, 1.0),
        PredefinedCity("Brest", "France", 48.3904, -4.4861, 35.0, 1.0),
        PredefinedCity("Tours", "France", 47.3941, 0.6848, 60.0, 1.0),
        PredefinedCity("Amiens", "France", 49.8941, 2.2958, 34.0, 1.0),
        PredefinedCity("Limoges", "France", 45.8336, 1.2611, 300.0, 1.0),
        PredefinedCity("Perpignan", "France", 42.6886, 2.8948, 40.0, 1.0),
        PredefinedCity("Besançon", "France", 47.2378, 6.0241, 307.0, 1.0),
        PredefinedCity("Orléans", "France", 47.9029, 1.9039, 116.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // ITALIA (UTC+1) - 30 orașe
        // Rome (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Rome", "Italy", 41.9028, 12.4964, 21.0, 1.0),
        PredefinedCity("Milan", "Italy", 45.4642, 9.1900, 120.0, 1.0),
        PredefinedCity("Naples", "Italy", 40.8518, 14.2681, 17.0, 1.0),
        PredefinedCity("Turin", "Italy", 45.0703, 7.6869, 239.0, 1.0),
        PredefinedCity("Palermo", "Italy", 38.1157, 13.3615, 14.0, 1.0),
        PredefinedCity("Genoa", "Italy", 44.4056, 8.9463, 19.0, 1.0),
        PredefinedCity("Bologna", "Italy", 44.4949, 11.3426, 54.0, 1.0),
        PredefinedCity("Florence", "Italy", 43.7696, 11.2558, 50.0, 1.0),
        PredefinedCity("Bari", "Italy", 41.1171, 16.8719, 5.0, 1.0),
        PredefinedCity("Catania", "Italy", 37.5079, 15.0830, 7.0, 1.0),
        PredefinedCity("Venice", "Italy", 45.4408, 12.3155, 1.0, 1.0),
        PredefinedCity("Verona", "Italy", 45.4384, 10.9916, 59.0, 1.0),
        PredefinedCity("Messina", "Italy", 38.1938, 15.5540, 3.0, 1.0),
        PredefinedCity("Padua", "Italy", 45.4064, 11.8768, 12.0, 1.0),
        PredefinedCity("Trieste", "Italy", 45.6495, 13.7768, 2.0, 1.0),
        PredefinedCity("Brescia", "Italy", 45.5416, 10.2118, 149.0, 1.0),
        PredefinedCity("Parma", "Italy", 44.8015, 10.3279, 57.0, 1.0),
        PredefinedCity("Taranto", "Italy", 40.4644, 17.2470, 15.0, 1.0),
        PredefinedCity("Prato", "Italy", 43.8777, 11.1020, 61.0, 1.0),
        PredefinedCity("Modena", "Italy", 44.6471, 10.9252, 34.0, 1.0),
        PredefinedCity("Reggio Calabria", "Italy", 38.1147, 15.6501, 31.0, 1.0),
        PredefinedCity("Reggio Emilia", "Italy", 44.6989, 10.6297, 58.0, 1.0),
        PredefinedCity("Perugia", "Italy", 43.1107, 12.3908, 493.0, 1.0),
        PredefinedCity("Livorno", "Italy", 43.5485, 10.3106, 3.0, 1.0),
        PredefinedCity("Ravenna", "Italy", 44.4184, 12.2035, 4.0, 1.0),
        PredefinedCity("Cagliari", "Italy", 39.2238, 9.1217, 4.0, 1.0),
        PredefinedCity("Foggia", "Italy", 41.4621, 15.5444, 76.0, 1.0),
        PredefinedCity("Rimini", "Italy", 44.0678, 12.5695, 5.0, 1.0),
        PredefinedCity("Salerno", "Italy", 40.6824, 14.7681, 4.0, 1.0),
        PredefinedCity("Ferrara", "Italy", 44.8381, 11.6198, 9.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // SPANIA (UTC+1) - 20 orașe
        // Madrid (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Madrid", "Spain", 40.4168, -3.7038, 667.0, 1.0),
        PredefinedCity("Barcelona", "Spain", 41.3851, 2.1734, 12.0, 1.0),
        PredefinedCity("Valencia", "Spain", 39.4699, -0.3763, 15.0, 1.0),
        PredefinedCity("Seville", "Spain", 37.3891, -5.9845, 7.0, 1.0),
        PredefinedCity("Zaragoza", "Spain", 41.6488, -0.8891, 208.0, 1.0),
        PredefinedCity("Málaga", "Spain", 36.7213, -4.4214, 11.0, 1.0),
        PredefinedCity("Murcia", "Spain", 37.9922, -1.1307, 43.0, 1.0),
        PredefinedCity("Palma de Mallorca", "Spain", 39.5696, 2.6502, 13.0, 1.0),
        PredefinedCity("Las Palmas", "Spain", 28.1235, -15.4363, 8.0, 0.0),
        PredefinedCity("Bilbao", "Spain", 43.2630, -2.9350, 19.0, 1.0),
        PredefinedCity("Alicante", "Spain", 38.3452, -0.4810, 0.0, 1.0),
        PredefinedCity("Córdoba", "Spain", 37.8882, -4.7794, 106.0, 1.0),
        PredefinedCity("Valladolid", "Spain", 41.6523, -4.7245, 698.0, 1.0),
        PredefinedCity("Vigo", "Spain", 42.2406, -8.7207, 25.0, 1.0),
        PredefinedCity("Gijón", "Spain", 43.5453, -5.6635, 3.0, 1.0),
        PredefinedCity("Granada", "Spain", 37.1773, -3.5986, 738.0, 1.0),
        PredefinedCity("Vitoria-Gasteiz", "Spain", 42.8467, -2.6726, 525.0, 1.0),
        PredefinedCity("A Coruña", "Spain", 43.3623, -8.4115, 21.0, 1.0),
        PredefinedCity("San Sebastián", "Spain", 43.3183, -1.9812, 6.0, 1.0),
        PredefinedCity("Santander", "Spain", 43.4623, -3.8100, 15.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // REGATUL UNIT / UK (UTC+0) - 40 orașe
        // London (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("London", "United Kingdom", 51.5074, -0.1278, 11.0, 0.0),
        PredefinedCity("Birmingham", "United Kingdom", 52.4862, -1.8904, 140.0, 0.0),
        PredefinedCity("Manchester", "United Kingdom", 53.4808, -2.2426, 38.0, 0.0),
        PredefinedCity("Glasgow", "United Kingdom", 55.8642, -4.2518, 40.0, 0.0),
        PredefinedCity("Liverpool", "United Kingdom", 53.4084, -2.9916, 10.0, 0.0),
        PredefinedCity("Leeds", "United Kingdom", 53.8008, -1.5491, 63.0, 0.0),
        PredefinedCity("Sheffield", "United Kingdom", 53.3811, -1.4701, 75.0, 0.0),
        PredefinedCity("Edinburgh", "United Kingdom", 55.9533, -3.1883, 47.0, 0.0),
        PredefinedCity("Bristol", "United Kingdom", 51.4545, -2.5879, 11.0, 0.0),
        PredefinedCity("Leicester", "United Kingdom", 52.6369, -1.1398, 63.0, 0.0),
        PredefinedCity("Coventry", "United Kingdom", 52.4068, -1.5197, 81.0, 0.0),
        PredefinedCity("Bradford", "United Kingdom", 53.7960, -1.7594, 127.0, 0.0),
        PredefinedCity("Cardiff", "United Kingdom", 51.4816, -3.1791, 9.0, 0.0),
        PredefinedCity("Belfast", "United Kingdom", 54.5973, -5.9301, 11.0, 0.0),
        PredefinedCity("Nottingham", "United Kingdom", 52.9548, -1.1581, 39.0, 0.0),
        PredefinedCity("Kingston upon Hull", "United Kingdom", 53.7676, -0.3274, 2.0, 0.0),
        PredefinedCity("Newcastle", "United Kingdom", 54.9783, -1.6178, 45.0, 0.0),
        PredefinedCity("Stoke-on-Trent", "United Kingdom", 53.0027, -2.1794, 116.0, 0.0),
        PredefinedCity("Southampton", "United Kingdom", 50.9097, -1.4044, 3.0, 0.0),
        PredefinedCity("Derby", "United Kingdom", 52.9225, -1.4746, 75.0, 0.0),
        PredefinedCity("Portsmouth", "United Kingdom", 50.8198, -1.0880, 3.0, 0.0),
        PredefinedCity("Brighton", "United Kingdom", 50.8225, -0.1372, 15.0, 0.0),
        PredefinedCity("Plymouth", "United Kingdom", 50.3755, -4.1427, 50.0, 0.0),
        PredefinedCity("Wolverhampton", "United Kingdom", 52.5870, -2.1288, 134.0, 0.0),
        PredefinedCity("Reading", "United Kingdom", 51.4543, -0.9781, 36.0, 0.0),
        PredefinedCity("Aberdeen", "United Kingdom", 57.1497, -2.0943, 29.0, 0.0),
        PredefinedCity("Sunderland", "United Kingdom", 54.9069, -1.3838, 18.0, 0.0),
        PredefinedCity("Swansea", "United Kingdom", 51.6214, -3.9436, 9.0, 0.0),
        PredefinedCity("Oxford", "United Kingdom", 51.7520, -1.2577, 72.0, 0.0),
        PredefinedCity("Cambridge", "United Kingdom", 52.2053, 0.1218, 6.0, 0.0),
        PredefinedCity("York", "United Kingdom", 53.9600, -1.0873, 17.0, 0.0),
        PredefinedCity("Peterborough", "United Kingdom", 52.5695, -0.2405, 10.0, 0.0),
        PredefinedCity("Dundee", "United Kingdom", 56.4620, -2.9707, 42.0, 0.0),
        PredefinedCity("Lancaster", "United Kingdom", 54.0466, -2.8007, 17.0, 0.0),
        PredefinedCity("Bath", "United Kingdom", 51.3751, -2.3617, 18.0, 0.0),
        PredefinedCity("Exeter", "United Kingdom", 50.7260, -3.5275, 27.0, 0.0),
        PredefinedCity("Norwich", "United Kingdom", 52.6309, 1.2974, 18.0, 0.0),
        PredefinedCity("Cheltenham", "United Kingdom", 51.8994, -2.0783, 103.0, 0.0),
        PredefinedCity("Chester", "United Kingdom", 53.1905, -2.8909, 12.0, 0.0),
        PredefinedCity("Inverness", "United Kingdom", 57.4778, -4.2247, 10.0, 0.0),
        
        // ══════════════════════════════════════════════════════════
        // POLONIA (UTC+1) - 30 orașe
        // Warsaw (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Warsaw", "Poland", 52.2297, 21.0122, 113.0, 1.0),
        PredefinedCity("Kraków", "Poland", 50.0647, 19.9450, 219.0, 1.0),
        PredefinedCity("Łódź", "Poland", 51.7592, 19.4560, 278.0, 1.0),
        PredefinedCity("Wrocław", "Poland", 51.1079, 17.0385, 105.0, 1.0),
        PredefinedCity("Poznań", "Poland", 52.4064, 16.9252, 60.0, 1.0),
        PredefinedCity("Gdańsk", "Poland", 54.3520, 18.6466, 7.0, 1.0),
        PredefinedCity("Szczecin", "Poland", 53.4285, 14.5528, 1.0, 1.0),
        PredefinedCity("Bydgoszcz", "Poland", 53.1235, 18.0084, 60.0, 1.0),
        PredefinedCity("Lublin", "Poland", 51.2465, 22.5684, 163.0, 1.0),
        PredefinedCity("Białystok", "Poland", 53.1325, 23.1688, 120.0, 1.0),
        PredefinedCity("Katowice", "Poland", 50.2649, 19.0238, 284.0, 1.0),
        PredefinedCity("Gdynia", "Poland", 54.5189, 18.5305, 10.0, 1.0),
        PredefinedCity("Częstochowa", "Poland", 50.8118, 19.1203, 261.0, 1.0),
        PredefinedCity("Radom", "Poland", 51.4027, 21.1471, 177.0, 1.0),
        PredefinedCity("Sosnowiec", "Poland", 50.2863, 19.1042, 252.0, 1.0),
        PredefinedCity("Toruń", "Poland", 53.0138, 18.5984, 65.0, 1.0),
        PredefinedCity("Kielce", "Poland", 50.8661, 20.6286, 260.0, 1.0),
        PredefinedCity("Rzeszów", "Poland", 50.0412, 21.9991, 212.0, 1.0),
        PredefinedCity("Gliwice", "Poland", 50.2945, 18.6714, 232.0, 1.0),
        PredefinedCity("Zabrze", "Poland", 50.3249, 18.7857, 247.0, 1.0),
        PredefinedCity("Olsztyn", "Poland", 53.7784, 20.4801, 88.0, 1.0),
        PredefinedCity("Bielsko-Biała", "Poland", 49.8224, 19.0444, 398.0, 1.0),
        PredefinedCity("Bytom", "Poland", 50.3484, 18.9156, 267.0, 1.0),
        PredefinedCity("Zielona Góra", "Poland", 51.9356, 15.5062, 136.0, 1.0),
        PredefinedCity("Rybnik", "Poland", 50.1022, 18.5463, 255.0, 1.0),
        PredefinedCity("Ruda Śląska", "Poland", 50.2558, 18.8555, 269.0, 1.0),
        PredefinedCity("Opole", "Poland", 50.6751, 17.9213, 149.0, 1.0),
        PredefinedCity("Tychy", "Poland", 50.1371, 18.9640, 253.0, 1.0),
        PredefinedCity("Płock", "Poland", 52.5463, 19.7065, 58.0, 1.0),
        PredefinedCity("Elbląg", "Poland", 54.1522, 19.4088, 3.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // OLANDA / NETHERLANDS (UTC+1) - 20 orașe
        // Amsterdam (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Amsterdam", "Netherlands", 52.3676, 4.9041, 2.0, 1.0),
        PredefinedCity("Rotterdam", "Netherlands", 51.9244, 4.4777, 0.0, 1.0),
        PredefinedCity("The Hague", "Netherlands", 52.0705, 4.3007, 1.0, 1.0),
        PredefinedCity("Utrecht", "Netherlands", 52.0907, 5.1214, 2.0, 1.0),
        PredefinedCity("Eindhoven", "Netherlands", 51.4416, 5.4697, 17.0, 1.0),
        PredefinedCity("Tilburg", "Netherlands", 51.5555, 5.0913, 14.0, 1.0),
        PredefinedCity("Groningen", "Netherlands", 53.2194, 6.5665, 3.0, 1.0),
        PredefinedCity("Almere", "Netherlands", 52.3508, 5.2647, -3.0, 1.0),
        PredefinedCity("Breda", "Netherlands", 51.5719, 4.7683, 5.0, 1.0),
        PredefinedCity("Nijmegen", "Netherlands", 51.8126, 5.8372, 13.0, 1.0),
        PredefinedCity("Apeldoorn", "Netherlands", 52.2112, 5.9699, 18.0, 1.0),
        PredefinedCity("Haarlem", "Netherlands", 52.3874, 4.6462, 1.0, 1.0),
        PredefinedCity("Arnhem", "Netherlands", 51.9851, 5.8987, 13.0, 1.0),
        PredefinedCity("Enschede", "Netherlands", 52.2215, 6.8937, 34.0, 1.0),
        PredefinedCity("Amersfoort", "Netherlands", 52.1561, 5.3878, 4.0, 1.0),
        PredefinedCity("Zaanstad", "Netherlands", 52.4559, 4.8178, 0.0, 1.0),
        PredefinedCity("Maastricht", "Netherlands", 50.8514, 5.6910, 49.0, 1.0),
        PredefinedCity("Dordrecht", "Netherlands", 51.8133, 4.6901, 2.0, 1.0),
        PredefinedCity("Leiden", "Netherlands", 52.1601, 4.4970, 0.0, 1.0),
        PredefinedCity("Delft", "Netherlands", 52.0116, 4.3571, 1.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // BELGIA (UTC+1) - 20 orașe
        // Brussels (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Brussels", "Belgium", 50.8503, 4.3517, 13.0, 1.0),
        PredefinedCity("Antwerp", "Belgium", 51.2194, 4.4025, 7.0, 1.0),
        PredefinedCity("Ghent", "Belgium", 51.0543, 3.7174, 5.0, 1.0),
        PredefinedCity("Charleroi", "Belgium", 50.4108, 4.4446, 141.0, 1.0),
        PredefinedCity("Liège", "Belgium", 50.6326, 5.5797, 61.0, 1.0),
        PredefinedCity("Bruges", "Belgium", 51.2093, 3.2247, 7.0, 1.0),
        PredefinedCity("Namur", "Belgium", 50.4674, 4.8720, 89.0, 1.0),
        PredefinedCity("Leuven", "Belgium", 50.8798, 4.7005, 26.0, 1.0),
        PredefinedCity("Mons", "Belgium", 50.4542, 3.9523, 53.0, 1.0),
        PredefinedCity("Mechelen", "Belgium", 51.0259, 4.4776, 8.0, 1.0),
        PredefinedCity("Aalst", "Belgium", 50.9369, 4.0355, 16.0, 1.0),
        PredefinedCity("Hasselt", "Belgium", 50.9307, 5.3378, 38.0, 1.0),
        PredefinedCity("Kortrijk", "Belgium", 50.8279, 3.2649, 14.0, 1.0),
        PredefinedCity("Sint-Niklaas", "Belgium", 51.1565, 4.1431, 11.0, 1.0),
        PredefinedCity("Ostend", "Belgium", 51.2154, 2.9286, 5.0, 1.0),
        PredefinedCity("Tournai", "Belgium", 50.6058, 3.3883, 28.0, 1.0),
        PredefinedCity("Genk", "Belgium", 50.9654, 5.5015, 62.0, 1.0),
        PredefinedCity("Seraing", "Belgium", 50.5836, 5.5078, 70.0, 1.0),
        PredefinedCity("Roeselare", "Belgium", 50.9446, 3.1260, 21.0, 1.0),
        PredefinedCity("Verviers", "Belgium", 50.5891, 5.8662, 208.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // PORTUGALIA (UTC+0) - 20 orașe
        // Lisbon (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Lisbon", "Portugal", 38.7223, -9.1393, 2.0, 0.0),
        PredefinedCity("Porto", "Portugal", 41.1579, -8.6291, 104.0, 0.0),
        PredefinedCity("Vila Nova de Gaia", "Portugal", 41.1239, -8.6118, 85.0, 0.0),
        PredefinedCity("Amadora", "Portugal", 38.7597, -9.2395, 100.0, 0.0),
        PredefinedCity("Braga", "Portugal", 41.5454, -8.4265, 190.0, 0.0),
        PredefinedCity("Setúbal", "Portugal", 38.5254, -8.8941, 8.0, 0.0),
        PredefinedCity("Coimbra", "Portugal", 40.2033, -8.4103, 75.0, 0.0),
        PredefinedCity("Funchal", "Portugal", 32.6669, -16.9241, 3.0, 0.0),
        PredefinedCity("Almada", "Portugal", 38.6790, -9.1565, 41.0, 0.0),
        PredefinedCity("Agualva-Cacém", "Portugal", 38.7706, -9.2957, 130.0, 0.0),
        PredefinedCity("Queluz", "Portugal", 38.7566, -9.2546, 125.0, 0.0),
        PredefinedCity("Guimarães", "Portugal", 41.4425, -8.2918, 175.0, 0.0),
        PredefinedCity("Leiria", "Portugal", 39.7436, -8.8071, 50.0, 0.0),
        PredefinedCity("Aveiro", "Portugal", 40.6443, -8.6455, 5.0, 0.0),
        PredefinedCity("Évora", "Portugal", 38.5667, -7.9000, 309.0, 0.0),
        PredefinedCity("Faro", "Portugal", 37.0194, -7.9322, 8.0, 0.0),
        PredefinedCity("Viseu", "Portugal", 40.6610, -7.9097, 443.0, 0.0),
        PredefinedCity("Santarém", "Portugal", 39.2369, -8.6870, 103.0, 0.0),
        PredefinedCity("Viana do Castelo", "Portugal", 41.6918, -8.8344, 12.0, 0.0),
        PredefinedCity("Ponta Delgada", "Portugal", 37.7394, -25.6687, 35.0, -1.0),

        // ══════════════════════════════════════════════════════════
        // AUSTRIA (UTC+1) - 30 orașe
        // Vienna (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Vienna", "Austria", 48.2082, 16.3738, 171.0, 1.0),
        PredefinedCity("Graz", "Austria", 47.0707, 15.4395, 353.0, 1.0),
        PredefinedCity("Linz", "Austria", 48.3069, 14.2858, 266.0, 1.0),
        PredefinedCity("Salzburg", "Austria", 47.8095, 13.0550, 424.0, 1.0),
        PredefinedCity("Innsbruck", "Austria", 47.2692, 11.4041, 574.0, 1.0),
        PredefinedCity("Klagenfurt", "Austria", 46.6365, 14.3122, 446.0, 1.0),
        PredefinedCity("Villach", "Austria", 46.6111, 13.8558, 501.0, 1.0),
        PredefinedCity("Wels", "Austria", 48.1575, 14.0286, 317.0, 1.0),
        PredefinedCity("Sankt Pölten", "Austria", 48.2047, 15.6256, 267.0, 1.0),
        PredefinedCity("Dornbirn", "Austria", 47.4125, 9.7417, 437.0, 1.0),
        PredefinedCity("Wiener Neustadt", "Austria", 47.8139, 16.2465, 265.0, 1.0),
        PredefinedCity("Steyr", "Austria", 48.0425, 14.4212, 310.0, 1.0),
        PredefinedCity("Feldkirch", "Austria", 47.2399, 9.5987, 458.0, 1.0),
        PredefinedCity("Bregenz", "Austria", 47.5031, 9.7471, 398.0, 1.0),
        PredefinedCity("Leonding", "Austria", 48.2600, 14.2503, 298.0, 1.0),
        PredefinedCity("Klosterneuburg", "Austria", 48.3053, 16.3256, 192.0, 1.0),
        PredefinedCity("Baden", "Austria", 48.0069, 16.2308, 230.0, 1.0),
        PredefinedCity("Wolfsberg", "Austria", 46.8406, 14.8408, 463.0, 1.0),
        PredefinedCity("Leoben", "Austria", 47.3765, 15.0918, 541.0, 1.0),
        PredefinedCity("Krems", "Austria", 48.4092, 15.6142, 190.0, 1.0),
        PredefinedCity("Traun", "Austria", 48.2228, 14.2386, 271.0, 1.0),
        PredefinedCity("Amstetten", "Austria", 48.1228, 14.8747, 275.0, 1.0),
        PredefinedCity("Lustenau", "Austria", 47.4264, 9.6581, 405.0, 1.0),
        PredefinedCity("Kapfenberg", "Austria", 47.4439, 15.2925, 502.0, 1.0),
        PredefinedCity("Mödling", "Austria", 48.0856, 16.2847, 234.0, 1.0),
        PredefinedCity("Hallein", "Austria", 47.6833, 13.0903, 450.0, 1.0),
        PredefinedCity("Kufstein", "Austria", 47.5833, 12.1667, 499.0, 1.0),
        PredefinedCity("Traiskirchen", "Austria", 48.0167, 16.2833, 200.0, 1.0),
        PredefinedCity("Schwechat", "Austria", 48.1381, 16.4708, 153.0, 1.0),
        PredefinedCity("Braunau am Inn", "Austria", 48.2567, 13.0333, 352.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // ELVEȚIA / SWITZERLAND (UTC+1) - 10 orașe
        // Bern (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Bern", "Switzerland", 46.9480, 7.4474, 540.0, 1.0),
        PredefinedCity("Zürich", "Switzerland", 47.3769, 8.5417, 408.0, 1.0),
        PredefinedCity("Geneva", "Switzerland", 46.2044, 6.1432, 375.0, 1.0),
        PredefinedCity("Basel", "Switzerland", 47.5596, 7.5886, 260.0, 1.0),
        PredefinedCity("Lausanne", "Switzerland", 46.5197, 6.6323, 495.0, 1.0),
        PredefinedCity("Winterthur", "Switzerland", 47.5001, 8.7501, 439.0, 1.0),
        PredefinedCity("Lucerne", "Switzerland", 47.0502, 8.3093, 435.0, 1.0),
        PredefinedCity("St. Gallen", "Switzerland", 47.4245, 9.3767, 675.0, 1.0),
        PredefinedCity("Lugano", "Switzerland", 46.0037, 8.9511, 273.0, 1.0),
        PredefinedCity("Biel/Bienne", "Switzerland", 47.1368, 7.2467, 434.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // CEHIA / CZECH REPUBLIC (UTC+1) - 20 orașe
        // Prague (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Prague", "Czech Republic", 50.0755, 14.4378, 235.0, 1.0),
        PredefinedCity("Brno", "Czech Republic", 49.1951, 16.6068, 190.0, 1.0),
        PredefinedCity("Ostrava", "Czech Republic", 49.8209, 18.2625, 208.0, 1.0),
        PredefinedCity("Plzeň", "Czech Republic", 49.7384, 13.3736, 311.0, 1.0),
        PredefinedCity("Liberec", "Czech Republic", 50.7663, 15.0543, 357.0, 1.0),
        PredefinedCity("Olomouc", "Czech Republic", 49.5938, 17.2509, 219.0, 1.0),
        PredefinedCity("České Budějovice", "Czech Republic", 48.9746, 14.4747, 381.0, 1.0),
        PredefinedCity("Hradec Králové", "Czech Republic", 50.2104, 15.8252, 235.0, 1.0),
        PredefinedCity("Ústí nad Labem", "Czech Republic", 50.6607, 14.0323, 145.0, 1.0),
        PredefinedCity("Pardubice", "Czech Republic", 50.0343, 15.7812, 225.0, 1.0),
        PredefinedCity("Zlín", "Czech Republic", 49.2331, 17.6669, 230.0, 1.0),
        PredefinedCity("Havířov", "Czech Republic", 49.7797, 18.4370, 275.0, 1.0),
        PredefinedCity("Kladno", "Czech Republic", 50.1433, 14.1053, 381.0, 1.0),
        PredefinedCity("Most", "Czech Republic", 50.5030, 13.6364, 230.0, 1.0),
        PredefinedCity("Opava", "Czech Republic", 49.9388, 17.9025, 257.0, 1.0),
        PredefinedCity("Frýdek-Místek", "Czech Republic", 49.6880, 18.3503, 304.0, 1.0),
        PredefinedCity("Karviná", "Czech Republic", 49.8542, 18.5428, 230.0, 1.0),
        PredefinedCity("Jihlava", "Czech Republic", 49.3961, 15.5912, 525.0, 1.0),
        PredefinedCity("Teplice", "Czech Republic", 50.6404, 13.8244, 220.0, 1.0),
        PredefinedCity("Karlovy Vary", "Czech Republic", 50.2325, 12.8713, 376.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // UNGARIA / HUNGARY (UTC+1) - 20 orașe
        // Budapest (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Budapest", "Hungary", 47.4979, 19.0402, 96.0, 1.0),
        PredefinedCity("Debrecen", "Hungary", 47.5316, 21.6273, 108.0, 1.0),
        PredefinedCity("Szeged", "Hungary", 46.2530, 20.1414, 75.0, 1.0),
        PredefinedCity("Miskolc", "Hungary", 48.1035, 20.7784, 130.0, 1.0),
        PredefinedCity("Pécs", "Hungary", 46.0727, 18.2323, 153.0, 1.0),
        PredefinedCity("Győr", "Hungary", 47.6875, 17.6504, 108.0, 1.0),
        PredefinedCity("Nyíregyháza", "Hungary", 47.9553, 21.7167, 111.0, 1.0),
        PredefinedCity("Kecskemét", "Hungary", 46.9062, 19.6913, 120.0, 1.0),
        PredefinedCity("Székesfehérvár", "Hungary", 47.1860, 18.4221, 108.0, 1.0),
        PredefinedCity("Szombathely", "Hungary", 47.2307, 16.6218, 209.0, 1.0),
        PredefinedCity("Szolnok", "Hungary", 47.1621, 20.1825, 88.0, 1.0),
        PredefinedCity("Tatabánya", "Hungary", 47.5863, 18.3949, 196.0, 1.0),
        PredefinedCity("Kaposvár", "Hungary", 46.3594, 17.7968, 143.0, 1.0),
        PredefinedCity("Érd", "Hungary", 47.3919, 18.9131, 105.0, 1.0),
        PredefinedCity("Veszprém", "Hungary", 47.1028, 17.9093, 240.0, 1.0),
        PredefinedCity("Békéscsaba", "Hungary", 46.6736, 21.0877, 89.0, 1.0),
        PredefinedCity("Zalaegerszeg", "Hungary", 46.8417, 16.8417, 168.0, 1.0),
        PredefinedCity("Sopron", "Hungary", 47.6817, 16.5845, 210.0, 1.0),
        PredefinedCity("Eger", "Hungary", 47.9026, 20.3772, 165.0, 1.0),
        PredefinedCity("Nagykanizsa", "Hungary", 46.4590, 16.9897, 141.0, 1.0),
		
		
		// ══════════════════════════════════════════════════════════
        // SUEDIA / SWEDEN (UTC+1) - 30 orașe
        // Stockholm (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Stockholm", "Sweden", 59.3293, 18.0686, 28.0, 1.0),
        PredefinedCity("Gothenburg", "Sweden", 57.7089, 11.9746, 12.0, 1.0),
        PredefinedCity("Malmö", "Sweden", 55.6050, 13.0038, 12.0, 1.0),
        PredefinedCity("Uppsala", "Sweden", 59.8586, 17.6389, 25.0, 1.0),
        PredefinedCity("Västerås", "Sweden", 59.6099, 16.5448, 12.0, 1.0),
        PredefinedCity("Örebro", "Sweden", 59.2753, 15.2134, 27.0, 1.0),
        PredefinedCity("Linköping", "Sweden", 58.4108, 15.6214, 55.0, 1.0),
        PredefinedCity("Helsingborg", "Sweden", 56.0465, 12.6945, 6.0, 1.0),
        PredefinedCity("Jönköping", "Sweden", 57.7826, 14.1618, 108.0, 1.0),
        PredefinedCity("Norrköping", "Sweden", 58.5877, 16.1924, 27.0, 1.0),
        PredefinedCity("Lund", "Sweden", 55.7047, 13.1910, 59.0, 1.0),
        PredefinedCity("Umeå", "Sweden", 63.8258, 20.2630, 12.0, 1.0),
        PredefinedCity("Gävle", "Sweden", 60.6749, 17.1413, 12.0, 1.0),
        PredefinedCity("Borås", "Sweden", 57.7210, 12.9401, 160.0, 1.0),
        PredefinedCity("Södertälje", "Sweden", 59.1955, 17.6253, 5.0, 1.0),
        PredefinedCity("Eskilstuna", "Sweden", 59.3666, 16.5077, 25.0, 1.0),
        PredefinedCity("Karlstad", "Sweden", 59.3793, 13.5036, 52.0, 1.0),
        PredefinedCity("Täby", "Sweden", 59.4439, 18.0687, 35.0, 1.0),
        PredefinedCity("Växjö", "Sweden", 56.8777, 14.8091, 166.0, 1.0),
        PredefinedCity("Halmstad", "Sweden", 56.6745, 12.8578, 5.0, 1.0),
        PredefinedCity("Sundsvall", "Sweden", 62.3908, 17.3069, 7.0, 1.0),
        PredefinedCity("Luleå", "Sweden", 65.5848, 22.1547, 10.0, 1.0),
        PredefinedCity("Trollhättan", "Sweden", 58.2837, 12.2886, 45.0, 1.0),
        PredefinedCity("Östersund", "Sweden", 63.1792, 14.6357, 325.0, 1.0),
        PredefinedCity("Kristianstad", "Sweden", 56.0294, 14.1567, 5.0, 1.0),
        PredefinedCity("Kalmar", "Sweden", 56.6634, 16.3566, 8.0, 1.0),
        PredefinedCity("Skellefteå", "Sweden", 64.7507, 20.9528, 20.0, 1.0),
        PredefinedCity("Karlskrona", "Sweden", 56.1612, 15.5869, 5.0, 1.0),
        PredefinedCity("Falun", "Sweden", 60.6065, 15.6355, 160.0, 1.0),
        PredefinedCity("Kiruna", "Sweden", 67.8558, 20.2253, 530.0, 1.0),
		PredefinedCity("Rävemåla", "Sweden", 56.4667, 14.4667, 150.0, 1.0),
		PredefinedCity("Tingsryd", "Sweden", 56.5267, 14.9800, 160.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // DANEMARCA / DENMARK (UTC+1) - 30 orașe
        // Copenhagen (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Copenhagen", "Denmark", 55.6761, 12.5683, 14.0, 1.0),
        PredefinedCity("Aarhus", "Denmark", 56.1629, 10.2039, 0.0, 1.0),
        PredefinedCity("Odense", "Denmark", 55.4038, 10.4024, 13.0, 1.0),
        PredefinedCity("Aalborg", "Denmark", 57.0488, 9.9217, 3.0, 1.0),
        PredefinedCity("Frederiksberg", "Denmark", 55.6786, 12.5251, 22.0, 1.0),
        PredefinedCity("Esbjerg", "Denmark", 55.4670, 8.4520, 17.0, 1.0),
        PredefinedCity("Randers", "Denmark", 56.4607, 10.0365, 15.0, 1.0),
        PredefinedCity("Kolding", "Denmark", 55.4904, 9.4722, 43.0, 1.0),
        PredefinedCity("Horsens", "Denmark", 55.8607, 9.8503, 5.0, 1.0),
        PredefinedCity("Vejle", "Denmark", 55.7113, 9.5364, 12.0, 1.0),
        PredefinedCity("Roskilde", "Denmark", 55.6415, 12.0803, 30.0, 1.0),
        PredefinedCity("Herning", "Denmark", 56.1394, 8.9756, 57.0, 1.0),
        PredefinedCity("Silkeborg", "Denmark", 56.1697, 9.5453, 45.0, 1.0),
        PredefinedCity("Næstved", "Denmark", 55.2259, 11.7600, 16.0, 1.0),
        PredefinedCity("Fredericia", "Denmark", 55.5658, 9.7526, 10.0, 1.0),
        PredefinedCity("Viborg", "Denmark", 56.4532, 9.4020, 30.0, 1.0),
        PredefinedCity("Køge", "Denmark", 55.4581, 12.1820, 8.0, 1.0),
        PredefinedCity("Holstebro", "Denmark", 56.3600, 8.6162, 27.0, 1.0),
        PredefinedCity("Slagelse", "Denmark", 55.4028, 11.3546, 30.0, 1.0),
        PredefinedCity("Helsingør", "Denmark", 56.0361, 12.6136, 12.0, 1.0),
        PredefinedCity("Hillerød", "Denmark", 55.9298, 12.3103, 35.0, 1.0),
        PredefinedCity("Sønderborg", "Denmark", 54.9094, 9.7920, 10.0, 1.0),
        PredefinedCity("Svendborg", "Denmark", 55.0596, 10.6068, 5.0, 1.0),
        PredefinedCity("Hjørring", "Denmark", 57.4642, 9.9826, 25.0, 1.0),
        PredefinedCity("Frederikshavn", "Denmark", 57.4407, 10.5364, 8.0, 1.0),
        PredefinedCity("Haderslev", "Denmark", 55.2536, 9.4893, 12.0, 1.0),
        PredefinedCity("Skive", "Denmark", 56.5675, 9.0297, 10.0, 1.0),
        PredefinedCity("Ringsted", "Denmark", 55.4427, 11.7901, 32.0, 1.0),
        PredefinedCity("Nyborg", "Denmark", 55.3127, 10.7896, 5.0, 1.0),
        PredefinedCity("Aabenraa", "Denmark", 55.0444, 9.4175, 10.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // NORVEGIA / NORWAY (UTC+1) - 20 orașe
        // Oslo (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Oslo", "Norway", 59.9139, 10.7522, 23.0, 1.0),
        PredefinedCity("Bergen", "Norway", 60.3913, 5.3221, 12.0, 1.0),
        PredefinedCity("Trondheim", "Norway", 63.4305, 10.3951, 20.0, 1.0),
        PredefinedCity("Stavanger", "Norway", 58.9700, 5.7331, 10.0, 1.0),
        PredefinedCity("Drammen", "Norway", 59.7441, 10.2045, 5.0, 1.0),
        PredefinedCity("Fredrikstad", "Norway", 59.2181, 10.9298, 5.0, 1.0),
        PredefinedCity("Kristiansand", "Norway", 58.1599, 8.0182, 7.0, 1.0),
        PredefinedCity("Sandnes", "Norway", 58.8517, 5.7361, 14.0, 1.0),
        PredefinedCity("Tromsø", "Norway", 69.6496, 18.9560, 10.0, 1.0),
        PredefinedCity("Sarpsborg", "Norway", 59.2839, 11.1095, 20.0, 1.0),
        PredefinedCity("Skien", "Norway", 59.2099, 9.6099, 10.0, 1.0),
        PredefinedCity("Ålesund", "Norway", 62.4722, 6.1495, 5.0, 1.0),
        PredefinedCity("Sandefjord", "Norway", 59.1318, 10.2168, 15.0, 1.0),
        PredefinedCity("Haugesund", "Norway", 59.4138, 5.2680, 5.0, 1.0),
        PredefinedCity("Tønsberg", "Norway", 59.2677, 10.4076, 12.0, 1.0),
        PredefinedCity("Moss", "Norway", 59.4340, 10.6590, 10.0, 1.0),
        PredefinedCity("Porsgrunn", "Norway", 59.1406, 9.6560, 8.0, 1.0),
        PredefinedCity("Bodø", "Norway", 67.2804, 14.4049, 13.0, 1.0),
        PredefinedCity("Hamar", "Norway", 60.7945, 11.0679, 128.0, 1.0),
        PredefinedCity("Larvik", "Norway", 59.0530, 10.0271, 10.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // FINLANDA / FINLAND (UTC+2) - 10 orașe
        // Helsinki (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Helsinki", "Finland", 60.1699, 24.9384, 26.0, 2.0),
        PredefinedCity("Espoo", "Finland", 60.2055, 24.6559, 32.0, 2.0),
        PredefinedCity("Tampere", "Finland", 61.4978, 23.7610, 114.0, 2.0),
        PredefinedCity("Vantaa", "Finland", 60.2934, 25.0378, 51.0, 2.0),
        PredefinedCity("Oulu", "Finland", 65.0121, 25.4651, 15.0, 2.0),
        PredefinedCity("Turku", "Finland", 60.4518, 22.2666, 3.0, 2.0),
        PredefinedCity("Jyväskylä", "Finland", 62.2426, 25.7473, 107.0, 2.0),
        PredefinedCity("Lahti", "Finland", 60.9827, 25.6612, 88.0, 2.0),
        PredefinedCity("Kuopio", "Finland", 62.8924, 27.6770, 95.0, 2.0),
        PredefinedCity("Pori", "Finland", 61.4847, 21.7972, 11.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // IRLANDA / IRELAND (UTC+0) - 10 orașe
        // Dublin (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Dublin", "Ireland", 53.3498, -6.2603, 8.0, 0.0),
        PredefinedCity("Cork", "Ireland", 51.8985, -8.4756, 10.0, 0.0),
        PredefinedCity("Limerick", "Ireland", 52.6638, -8.6267, 14.0, 0.0),
        PredefinedCity("Galway", "Ireland", 53.2707, -9.0568, 7.0, 0.0),
        PredefinedCity("Waterford", "Ireland", 52.2593, -7.1101, 8.0, 0.0),
        PredefinedCity("Drogheda", "Ireland", 53.7189, -6.3478, 18.0, 0.0),
        PredefinedCity("Swords", "Ireland", 53.4597, -6.2181, 40.0, 0.0),
        PredefinedCity("Dundalk", "Ireland", 54.0027, -6.4025, 7.0, 0.0),
        PredefinedCity("Bray", "Ireland", 53.2028, -6.0986, 10.0, 0.0),
        PredefinedCity("Kilkenny", "Ireland", 52.6541, -7.2448, 66.0, 0.0),

        // ══════════════════════════════════════════════════════════
        // GRECIA / GREECE (UTC+2) - 20 orașe
        // Athens (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Athens", "Greece", 37.9838, 23.7275, 70.0, 2.0),
        PredefinedCity("Thessaloniki", "Greece", 40.6401, 22.9444, 10.0, 2.0),
        PredefinedCity("Patras", "Greece", 38.2466, 21.7346, 5.0, 2.0),
        PredefinedCity("Piraeus", "Greece", 37.9475, 23.6461, 10.0, 2.0),
        PredefinedCity("Larissa", "Greece", 39.6390, 22.4191, 70.0, 2.0),
        PredefinedCity("Heraklion", "Greece", 35.3387, 25.1442, 35.0, 2.0),
        PredefinedCity("Peristeri", "Greece", 38.0168, 23.6914, 50.0, 2.0),
        PredefinedCity("Kallithea", "Greece", 37.9500, 23.7000, 20.0, 2.0),
        PredefinedCity("Volos", "Greece", 39.3666, 22.9507, 3.0, 2.0),
        PredefinedCity("Nikaia", "Greece", 37.9667, 23.6333, 15.0, 2.0),
        PredefinedCity("Kalamaria", "Greece", 40.5856, 22.9547, 25.0, 2.0),
        PredefinedCity("Glyfada", "Greece", 37.8667, 23.7500, 10.0, 2.0),
        PredefinedCity("Ioannina", "Greece", 39.6650, 20.8537, 480.0, 2.0),
        PredefinedCity("Chania", "Greece", 35.5138, 24.0180, 5.0, 2.0),
        PredefinedCity("Agrinio", "Greece", 38.6218, 21.4077, 100.0, 2.0),
        PredefinedCity("Kavala", "Greece", 40.9398, 24.4128, 15.0, 2.0),
        PredefinedCity("Lamia", "Greece", 38.8996, 22.4341, 75.0, 2.0),
        PredefinedCity("Komotini", "Greece", 41.1223, 25.4062, 40.0, 2.0),
        PredefinedCity("Rhodes", "Greece", 36.4349, 28.2176, 10.0, 2.0),
        PredefinedCity("Alexandroupoli", "Greece", 40.8476, 25.8743, 7.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // BULGARIA (UTC+2) - 20 orașe
        // Sofia (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Sofia", "Bulgaria", 42.6977, 23.3219, 550.0, 2.0),
        PredefinedCity("Plovdiv", "Bulgaria", 42.1354, 24.7453, 164.0, 2.0),
        PredefinedCity("Varna", "Bulgaria", 43.2141, 27.9147, 80.0, 2.0),
        PredefinedCity("Burgas", "Bulgaria", 42.5048, 27.4626, 16.0, 2.0),
        PredefinedCity("Ruse", "Bulgaria", 43.8356, 25.9657, 45.0, 2.0),
        PredefinedCity("Stara Zagora", "Bulgaria", 42.4258, 25.6345, 196.0, 2.0),
        PredefinedCity("Pleven", "Bulgaria", 43.4170, 24.6067, 120.0, 2.0),
        PredefinedCity("Sliven", "Bulgaria", 42.6816, 26.3292, 255.0, 2.0),
        PredefinedCity("Dobrich", "Bulgaria", 43.5667, 27.8333, 220.0, 2.0),
        PredefinedCity("Shumen", "Bulgaria", 43.2708, 26.9225, 225.0, 2.0),
        PredefinedCity("Pernik", "Bulgaria", 42.6050, 23.0378, 700.0, 2.0),
        PredefinedCity("Haskovo", "Bulgaria", 41.9344, 25.5554, 196.0, 2.0),
        PredefinedCity("Yambol", "Bulgaria", 42.4839, 26.5036, 125.0, 2.0),
        PredefinedCity("Pazardzhik", "Bulgaria", 42.0117, 24.3333, 213.0, 2.0),
        PredefinedCity("Blagoevgrad", "Bulgaria", 42.0162, 23.0951, 410.0, 2.0),
        PredefinedCity("Veliko Tarnovo", "Bulgaria", 43.0757, 25.6172, 208.0, 2.0),
        PredefinedCity("Vratsa", "Bulgaria", 43.2100, 23.5625, 378.0, 2.0),
        PredefinedCity("Gabrovo", "Bulgaria", 42.8742, 25.3342, 392.0, 2.0),
        PredefinedCity("Vidin", "Bulgaria", 43.9900, 22.8817, 35.0, 2.0),
        PredefinedCity("Kardzhali", "Bulgaria", 41.6333, 25.3667, 330.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // CROAȚIA / CROATIA (UTC+1) - 10 orașe
        // Zagreb (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Zagreb", "Croatia", 45.8150, 15.9819, 122.0, 1.0),
        PredefinedCity("Split", "Croatia", 43.5081, 16.4402, 0.0, 1.0),
        PredefinedCity("Rijeka", "Croatia", 45.3271, 14.4422, 12.0, 1.0),
        PredefinedCity("Osijek", "Croatia", 45.5550, 18.6955, 94.0, 1.0),
        PredefinedCity("Zadar", "Croatia", 44.1194, 15.2314, 5.0, 1.0),
        PredefinedCity("Pula", "Croatia", 44.8666, 13.8496, 30.0, 1.0),
        PredefinedCity("Slavonski Brod", "Croatia", 45.1603, 18.0156, 96.0, 1.0),
        PredefinedCity("Karlovac", "Croatia", 45.4929, 15.5553, 112.0, 1.0),
        PredefinedCity("Varaždin", "Croatia", 46.3057, 16.3366, 173.0, 1.0),
        PredefinedCity("Dubrovnik", "Croatia", 42.6507, 18.0944, 3.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // SERBIA (UTC+1) - 10 orașe
        // Belgrade (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Belgrade", "Serbia", 44.7866, 20.4489, 117.0, 1.0),
        PredefinedCity("Novi Sad", "Serbia", 45.2671, 19.8335, 80.0, 1.0),
        PredefinedCity("Niš", "Serbia", 43.3209, 21.8958, 194.0, 1.0),
        PredefinedCity("Kragujevac", "Serbia", 44.0128, 20.9114, 185.0, 1.0),
        PredefinedCity("Subotica", "Serbia", 46.1003, 19.6675, 114.0, 1.0),
        PredefinedCity("Zrenjanin", "Serbia", 45.3816, 20.3903, 80.0, 1.0),
        PredefinedCity("Pančevo", "Serbia", 44.8708, 20.6403, 77.0, 1.0),
        PredefinedCity("Čačak", "Serbia", 43.8914, 20.3497, 242.0, 1.0),
        PredefinedCity("Novi Pazar", "Serbia", 43.1367, 20.5122, 496.0, 1.0),
        PredefinedCity("Leskovac", "Serbia", 42.9981, 21.9461, 228.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // ROMÂNIA (UTC+2) - 147 localități 
        // București (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("București", "Romania", 44.4268, 26.1025, 80.0, 2.0),
        PredefinedCity("Cluj-Napoca", "Romania", 46.7712, 23.6236, 360.0, 2.0),
        PredefinedCity("Timișoara", "Romania", 45.7489, 21.2087, 90.0, 2.0),
        PredefinedCity("Iași", "Romania", 47.1585, 27.6014, 95.0, 2.0),
        PredefinedCity("Constanța", "Romania", 44.1598, 28.6348, 25.0, 2.0),
        PredefinedCity("Craiova", "Romania", 44.3302, 23.7949, 100.0, 2.0),
        PredefinedCity("Brașov", "Romania", 45.6579, 25.6012, 625.0, 2.0),
        PredefinedCity("Galați", "Romania", 45.4353, 28.0079, 55.0, 2.0),
        PredefinedCity("Ploiești", "Romania", 44.9417, 26.0236, 150.0, 2.0),
        PredefinedCity("Oradea", "Romania", 47.0722, 21.9217, 150.0, 2.0),
        PredefinedCity("Brăila", "Romania", 45.2692, 27.9575, 20.0, 2.0),
        PredefinedCity("Arad", "Romania", 46.1866, 21.3123, 117.0, 2.0),
        PredefinedCity("Pitești", "Romania", 44.8565, 24.8692, 287.0, 2.0),
        PredefinedCity("Sibiu", "Romania", 45.7928, 24.1521, 415.0, 2.0),
        PredefinedCity("Bacău", "Romania", 46.5711, 26.9253, 165.0, 2.0),
        PredefinedCity("Târgu Mureș", "Romania", 46.5425, 24.5575, 308.0, 2.0),
        PredefinedCity("Baia Mare", "Romania", 47.6530, 23.5795, 225.0, 2.0),
        PredefinedCity("Buzău", "Romania", 45.1500, 26.8333, 95.0, 2.0),
        PredefinedCity("Botoșani", "Romania", 47.7475, 26.6558, 150.0, 2.0),
        PredefinedCity("Satu Mare", "Romania", 47.7928, 22.8850, 123.0, 2.0),
        PredefinedCity("Râmnicu Vâlcea", "Romania", 45.0997, 24.3693, 243.0, 2.0),
        PredefinedCity("Drobeta-Turnu Severin", "Romania", 44.6322, 22.6561, 77.0, 2.0),
        PredefinedCity("Suceava", "Romania", 47.6635, 26.2732, 350.0, 2.0),
        PredefinedCity("Piatra Neamț", "Romania", 46.9275, 26.3708, 346.0, 2.0),
        PredefinedCity("Târgoviște", "Romania", 44.9254, 25.4567, 280.0, 2.0),
        PredefinedCity("Focșani", "Romania", 45.6969, 27.1858, 55.0, 2.0),
        PredefinedCity("Tulcea", "Romania", 45.1787, 28.8050, 50.0, 2.0),
        PredefinedCity("Reșița", "Romania", 45.3008, 21.8892, 240.0, 2.0),
        PredefinedCity("Târgu Jiu", "Romania", 45.0475, 23.2744, 210.0, 2.0),
        PredefinedCity("Bistrița", "Romania", 47.1358, 24.5000, 356.0, 2.0),
        PredefinedCity("Giurgiu", "Romania", 43.9037, 25.9699, 28.0, 2.0),
        PredefinedCity("Alba Iulia", "Romania", 46.0764, 23.5808, 245.0, 2.0),
        PredefinedCity("Deva", "Romania", 45.8833, 22.9000, 230.0, 2.0),
        PredefinedCity("Hunedoara", "Romania", 45.7597, 22.9203, 265.0, 2.0),
        PredefinedCity("Zalău", "Romania", 47.1922, 23.0575, 270.0, 2.0),
        PredefinedCity("Sfântu Gheorghe", "Romania", 45.8667, 25.7833, 520.0, 2.0),
        PredefinedCity("Alexandria", "Romania", 43.9700, 25.3333, 50.0, 2.0),
        PredefinedCity("Slobozia", "Romania", 44.5667, 27.3667, 30.0, 2.0),
        PredefinedCity("Mediaș", "Romania", 46.1667, 24.3500, 310.0, 2.0),
        PredefinedCity("Petroșani", "Romania", 45.4167, 23.3667, 610.0, 2.0),
        PredefinedCity("Roman", "Romania", 46.9228, 26.9297, 205.0, 2.0),
        PredefinedCity("Turda", "Romania", 46.5675, 23.7847, 345.0, 2.0),
        PredefinedCity("Bârlad", "Romania", 46.2333, 27.6667, 100.0, 2.0),
        PredefinedCity("Medgidia", "Romania", 44.2500, 28.2667, 35.0, 2.0),
        PredefinedCity("Miercurea Ciuc", "Romania", 46.3592, 25.8044, 662.0, 2.0),
        PredefinedCity("Sighișoara", "Romania", 46.2167, 24.7833, 400.0, 2.0),
        PredefinedCity("Mangalia", "Romania", 43.8167, 28.5833, 10.0, 2.0),
        PredefinedCity("Onești", "Romania", 46.2500, 26.7667, 230.0, 2.0),
        PredefinedCity("Odorheiu Secuiesc", "Romania", 46.3000, 25.2833, 527.0, 2.0),
        PredefinedCity("Câmpina", "Romania", 45.0333, 25.7333, 420.0, 2.0),
        PredefinedCity("Lugoj", "Romania", 45.6833, 21.9000, 123.0, 2.0),
        PredefinedCity("Caracal", "Romania", 44.1167, 24.3500, 106.0, 2.0),
        PredefinedCity("Slatina", "Romania", 44.4333, 24.3667, 155.0, 2.0),
        PredefinedCity("Câmpulung", "Romania", 45.2667, 25.0500, 630.0, 2.0),
        PredefinedCity("Reghin", "Romania", 46.7833, 24.7000, 390.0, 2.0),
        PredefinedCity("Mioveni", "Romania", 44.9500, 24.9333, 330.0, 2.0),
        PredefinedCity("Fălticeni", "Romania", 47.4667, 26.3000, 346.0, 2.0),
        PredefinedCity("Tecuci", "Romania", 45.8500, 27.4333, 65.0, 2.0),
        PredefinedCity("Câmpia Turzii", "Romania", 46.5500, 23.8833, 340.0, 2.0),
        PredefinedCity("Curtea de Argeș", "Romania", 45.1333, 24.6833, 455.0, 2.0),
        PredefinedCity("Rădăuți", "Romania", 47.8500, 25.9167, 375.0, 2.0),
        PredefinedCity("Pașcani", "Romania", 47.2500, 26.7167, 180.0, 2.0),
        PredefinedCity("Vaslui", "Romania", 46.6333, 27.7333, 115.0, 2.0),
        PredefinedCity("Caransebeș", "Romania", 45.4167, 22.2167, 210.0, 2.0),
        PredefinedCity("Huși", "Romania", 46.6667, 28.0500, 90.0, 2.0),
        PredefinedCity("Moreni", "Romania", 44.9833, 25.6500, 350.0, 2.0),
        PredefinedCity("Dorohoi", "Romania", 47.9500, 26.4000, 160.0, 2.0),
        PredefinedCity("Sebeș", "Romania", 45.9500, 23.5667, 260.0, 2.0),
        PredefinedCity("Oltenița", "Romania", 44.0833, 26.6333, 20.0, 2.0),
        PredefinedCity("Orăștie", "Romania", 45.8333, 23.2000, 280.0, 2.0),
        PredefinedCity("Vulcan", "Romania", 45.3833, 23.2667, 580.0, 2.0),
        PredefinedCity("Fetești", "Romania", 44.3667, 27.8333, 25.0, 2.0),
        PredefinedCity("Roșiorii de Vede", "Romania", 44.1000, 24.9833, 95.0, 2.0),
        PredefinedCity("Codlea", "Romania", 45.7000, 25.4500, 540.0, 2.0),
        PredefinedCity("Mărășești", "Romania", 45.8833, 27.2333, 75.0, 2.0),
        PredefinedCity("Lupeni", "Romania", 45.3500, 23.2333, 620.0, 2.0),
        PredefinedCity("Aiud", "Romania", 46.3167, 23.7333, 270.0, 2.0),
        PredefinedCity("Gheorgheni", "Romania", 46.7167, 25.5833, 798.0, 2.0),
        PredefinedCity("Năvodari", "Romania", 44.3167, 28.6167, 8.0, 2.0),
        PredefinedCity("Costinești", "Romania", 43.9509, 28.6012, 10.0, 2.0),
        PredefinedCity("Băile Herculane", "Romania", 44.8802, 22.4152, 168.0, 2.0),
        PredefinedCity("Șirnea", "Romania", 45.4714, 25.2547, 1150.0, 2.0),
        PredefinedCity("Bran", "Romania", 45.5150, 25.3675, 723.0, 2.0),
        PredefinedCity("Sinaia", "Romania", 45.3500, 25.5500, 798.0, 2.0),
        PredefinedCity("Bușteni", "Romania", 45.4167, 25.5333, 870.0, 2.0),
        PredefinedCity("Predeal", "Romania", 45.5000, 25.5833, 1040.0, 2.0),
        PredefinedCity("Azuga", "Romania", 45.4500, 25.5833, 950.0, 2.0),
        PredefinedCity("Poiana Brașov", "Romania", 45.5944, 25.5556, 1030.0, 2.0),
        PredefinedCity("Eforie Nord", "Romania", 44.0667, 28.6333, 15.0, 2.0),
        PredefinedCity("Eforie Sud", "Romania", 44.0333, 28.6500, 18.0, 2.0),
        PredefinedCity("Mamaia", "Romania", 44.2500, 28.6167, 3.0, 2.0),
        PredefinedCity("Vama Veche", "Romania", 43.7500, 28.5833, 5.0, 2.0),
        PredefinedCity("2 Mai", "Romania", 43.7667, 28.5667, 8.0, 2.0),
        PredefinedCity("Saturn", "Romania", 43.8333, 28.6000, 12.0, 2.0),
        PredefinedCity("Venus", "Romania", 43.8333, 28.6000, 10.0, 2.0),
        PredefinedCity("Jupiter", "Romania", 43.8167, 28.5833, 8.0, 2.0),
        PredefinedCity("Cap Aurora", "Romania", 43.8167, 28.5833, 5.0, 2.0),
        PredefinedCity("Neptun", "Romania", 43.8000, 28.6000, 3.0, 2.0),
        PredefinedCity("Olimp", "Romania", 43.8000, 28.6000, 5.0, 2.0),
        PredefinedCity("Adjud", "Romania", 46.1000, 27.1833, 170.0, 2.0),
        PredefinedCity("Blaj", "Romania", 46.1667, 23.9167, 296.0, 2.0),
        PredefinedCity("Buftea", "Romania", 44.5667, 25.9500, 92.0, 2.0),
        PredefinedCity("Breaza", "Romania", 45.1833, 25.6667, 420.0, 2.0),
        PredefinedCity("Cernavodă", "Romania", 44.3500, 28.0333, 25.0, 2.0),
        PredefinedCity("Cisnădie", "Romania", 45.7167, 24.1500, 443.0, 2.0),
        PredefinedCity("Comănești", "Romania", 46.4167, 26.4500, 490.0, 2.0),
        PredefinedCity("Covasna", "Romania", 45.8500, 26.1833, 564.0, 2.0),
        PredefinedCity("Dej", "Romania", 47.1500, 23.8667, 230.0, 2.0),
        PredefinedCity("Drăgășani", "Romania", 44.6667, 24.2500, 230.0, 2.0),
        PredefinedCity("Făgăraș", "Romania", 45.8500, 24.9833, 430.0, 2.0),
        PredefinedCity("Hațeg", "Romania", 45.6167, 22.9500, 280.0, 2.0),
        PredefinedCity("Horezu", "Romania", 45.1500, 24.0000, 480.0, 2.0),
        PredefinedCity("Măgurele", "Romania", 44.3500, 26.0333, 93.0, 2.0),
        PredefinedCity("Motru", "Romania", 44.8000, 22.9667, 270.0, 2.0),
        PredefinedCity("Negru Vodă", "Romania", 43.8333, 28.2333, 165.0, 2.0),
        PredefinedCity("Orșova", "Romania", 44.7167, 22.4000, 55.0, 2.0),
        PredefinedCity("Petrila", "Romania", 45.4500, 23.4167, 635.0, 2.0),
        PredefinedCity("Popești-Leordeni", "Romania", 44.3833, 26.1667, 85.0, 2.0),
        PredefinedCity("Râșnov", "Romania", 45.5833, 25.4667, 675.0, 2.0),
        PredefinedCity("Săcele", "Romania", 45.6167, 25.7000, 640.0, 2.0),
        PredefinedCity("Salonta", "Romania", 46.8000, 21.6500, 95.0, 2.0),
        PredefinedCity("Sângeorz-Băi", "Romania", 47.3667, 24.6667, 500.0, 2.0),
        PredefinedCity("Sighetu Marmației", "Romania", 47.9333, 23.8833, 274.0, 2.0),
        PredefinedCity("Sovata", "Romania", 46.6000, 25.0667, 500.0, 2.0),
        PredefinedCity("Târgu Neamț", "Romania", 47.2000, 26.3667, 360.0, 2.0),
        PredefinedCity("Târgu Ocna", "Romania", 46.2833, 26.6167, 284.0, 2.0),
        PredefinedCity("Târgu Secuiesc", "Romania", 46.0000, 26.1333, 530.0, 2.0),
        PredefinedCity("Târnăveni", "Romania", 46.3333, 24.2667, 350.0, 2.0),
        PredefinedCity("Techirghiol", "Romania", 44.0500, 28.6000, 15.0, 2.0),
        PredefinedCity("Toplița", "Romania", 46.9167, 25.3500, 650.0, 2.0),
        PredefinedCity("Turnu Măgurele", "Romania", 43.7500, 24.8667, 35.0, 2.0),
        PredefinedCity("Urziceni", "Romania", 44.7167, 26.6333, 52.0, 2.0),
        PredefinedCity("Vatra Dornei", "Romania", 47.3500, 25.3667, 810.0, 2.0),
        PredefinedCity("Vicovu de Sus", "Romania", 47.9333, 25.7000, 492.0, 2.0),
        PredefinedCity("Videle", "Romania", 44.2833, 25.5333, 110.0, 2.0),
        PredefinedCity("Viscri", "Romania", 46.0500, 25.0833, 470.0, 2.0),
        PredefinedCity("Voluntari", "Romania", 44.4833, 26.1833, 85.0, 2.0),
        PredefinedCity("Zimnicea", "Romania", 43.6500, 25.3667, 28.0, 2.0),
        PredefinedCity("Zărnești", "Romania", 45.5667, 25.3333, 640.0, 2.0),
		PredefinedCity("Vulcănești", "Romania", 45.6667, 27.4167, 350.0, 2.0),
		PredefinedCity("Vulcăneasa", "Romania", 45.7736, 26.9097, 257.0, 2.0),
		
		// ══════════════════════════════════════════════════════════
        // RUSIA / RUSSIA (UTC+3 pentru Moscova, variază pe regiuni)
        // Moscow (capitala), apoi descrescător după populație - 30 orașe
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Moscow", "Russia", 55.7558, 37.6173, 156.0, 3.0),
        PredefinedCity("Saint Petersburg", "Russia", 59.9343, 30.3351, 3.0, 3.0),
        PredefinedCity("Novosibirsk", "Russia", 55.0084, 82.9357, 161.0, 7.0),
        PredefinedCity("Yekaterinburg", "Russia", 56.8389, 60.6057, 270.0, 5.0),
        PredefinedCity("Kazan", "Russia", 55.8304, 49.0661, 116.0, 3.0),
        PredefinedCity("Nizhny Novgorod", "Russia", 56.2965, 43.9361, 150.0, 3.0),
        PredefinedCity("Chelyabinsk", "Russia", 55.1644, 61.4368, 219.0, 5.0),
        PredefinedCity("Samara", "Russia", 53.1959, 50.1002, 44.0, 4.0),
        PredefinedCity("Omsk", "Russia", 54.9885, 73.3242, 90.0, 6.0),
        PredefinedCity("Rostov-on-Don", "Russia", 47.2357, 39.7015, 74.0, 3.0),
        PredefinedCity("Ufa", "Russia", 54.7388, 55.9721, 150.0, 5.0),
        PredefinedCity("Krasnoyarsk", "Russia", 56.0153, 92.8932, 287.0, 7.0),
        PredefinedCity("Voronezh", "Russia", 51.6720, 39.1843, 156.0, 3.0),
        PredefinedCity("Perm", "Russia", 58.0105, 56.2502, 171.0, 5.0),
        PredefinedCity("Volgograd", "Russia", 48.7080, 44.5133, 80.0, 3.0),
        PredefinedCity("Krasnodar", "Russia", 45.0355, 38.9753, 25.0, 3.0),
        PredefinedCity("Saratov", "Russia", 51.5924, 45.9601, 50.0, 4.0),
        PredefinedCity("Tyumen", "Russia", 57.1522, 65.5272, 102.0, 5.0),
        PredefinedCity("Tolyatti", "Russia", 53.5303, 49.3461, 80.0, 4.0),
        PredefinedCity("Izhevsk", "Russia", 56.8498, 53.2045, 155.0, 4.0),
        PredefinedCity("Barnaul", "Russia", 53.3548, 83.7698, 190.0, 7.0),
        PredefinedCity("Ulyanovsk", "Russia", 54.3282, 48.3866, 80.0, 4.0),
        PredefinedCity("Irkutsk", "Russia", 52.2978, 104.2964, 440.0, 8.0),
        PredefinedCity("Khabarovsk", "Russia", 48.4827, 135.0838, 72.0, 10.0),
        PredefinedCity("Vladivostok", "Russia", 43.1332, 131.9113, 8.0, 10.0),
        PredefinedCity("Yaroslavl", "Russia", 57.6261, 39.8845, 98.0, 3.0),
        PredefinedCity("Makhachkala", "Russia", 42.9849, 47.5047, 15.0, 3.0),
        PredefinedCity("Tomsk", "Russia", 56.4846, 84.9476, 117.0, 7.0),
        PredefinedCity("Orenburg", "Russia", 51.7879, 55.0990, 107.0, 5.0),
        PredefinedCity("Kaliningrad", "Russia", 54.7104, 20.4522, 4.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // TURCIA / TURKEY (UTC+3) - 30 orașe
        // Ankara (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Ankara", "Turkey", 39.9334, 32.8597, 938.0, 3.0),
        PredefinedCity("Istanbul", "Turkey", 41.0082, 28.9784, 40.0, 3.0),
        PredefinedCity("Izmir", "Turkey", 38.4192, 27.1287, 2.0, 3.0),
        PredefinedCity("Bursa", "Turkey", 40.1885, 29.0610, 100.0, 3.0),
        PredefinedCity("Adana", "Turkey", 37.0000, 35.3213, 23.0, 3.0),
        PredefinedCity("Gaziantep", "Turkey", 37.0662, 37.3833, 855.0, 3.0),
        PredefinedCity("Konya", "Turkey", 37.8746, 32.4932, 1016.0, 3.0),
        PredefinedCity("Antalya", "Turkey", 36.8969, 30.7133, 30.0, 3.0),
        PredefinedCity("Kayseri", "Turkey", 38.7312, 35.4787, 1054.0, 3.0),
        PredefinedCity("Mersin", "Turkey", 36.8121, 34.6415, 5.0, 3.0),
        PredefinedCity("Eskişehir", "Turkey", 39.7767, 30.5206, 792.0, 3.0),
        PredefinedCity("Diyarbakır", "Turkey", 37.9144, 40.2306, 674.0, 3.0),
        PredefinedCity("Samsun", "Turkey", 41.2867, 36.3300, 4.0, 3.0),
        PredefinedCity("Denizli", "Turkey", 37.7833, 29.0947, 354.0, 3.0),
        PredefinedCity("Şanlıurfa", "Turkey", 37.1674, 38.7955, 518.0, 3.0),
        PredefinedCity("Adapazarı", "Turkey", 40.7000, 30.4000, 31.0, 3.0),
        PredefinedCity("Malatya", "Turkey", 38.3552, 38.3095, 964.0, 3.0),
        PredefinedCity("Kahramanmaraş", "Turkey", 37.5858, 36.9371, 568.0, 3.0),
        PredefinedCity("Erzurum", "Turkey", 39.9043, 41.2679, 1893.0, 3.0),
        PredefinedCity("Van", "Turkey", 38.4942, 43.3800, 1727.0, 3.0),
        PredefinedCity("Batman", "Turkey", 37.8812, 41.1351, 540.0, 3.0),
        PredefinedCity("Elazığ", "Turkey", 38.6810, 39.2264, 1067.0, 3.0),
        PredefinedCity("Trabzon", "Turkey", 41.0027, 39.7168, 39.0, 3.0),
        PredefinedCity("Sivas", "Turkey", 39.7477, 37.0179, 1285.0, 3.0),
        PredefinedCity("Manisa", "Turkey", 38.6191, 27.4289, 71.0, 3.0),
        PredefinedCity("Gebze", "Turkey", 40.8027, 29.4307, 35.0, 3.0),
        PredefinedCity("Bodrum", "Turkey", 37.0343, 27.4305, 26.0, 3.0),
        PredefinedCity("Marmaris", "Turkey", 36.8550, 28.2741, 16.0, 3.0),
        PredefinedCity("Fethiye", "Turkey", 36.6515, 29.1164, 5.0, 3.0),
        PredefinedCity("Kuşadası", "Turkey", 37.8579, 27.2610, 25.0, 3.0),

        // ══════════════════════════════════════════════════════════
        // UCRAINA / UKRAINE (UTC+2) - 30 orașe
        // Kyiv (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Kyiv", "Ukraine", 50.4501, 30.5234, 179.0, 2.0),
        PredefinedCity("Kharkiv", "Ukraine", 49.9935, 36.2304, 152.0, 2.0),
        PredefinedCity("Odesa", "Ukraine", 46.4825, 30.7233, 40.0, 2.0),
        PredefinedCity("Dnipro", "Ukraine", 48.4647, 35.0462, 68.0, 2.0),
        PredefinedCity("Donetsk", "Ukraine", 48.0159, 37.8029, 169.0, 2.0),
        PredefinedCity("Zaporizhzhia", "Ukraine", 47.8388, 35.1396, 50.0, 2.0),
        PredefinedCity("Lviv", "Ukraine", 49.8397, 24.0297, 289.0, 2.0),
        PredefinedCity("Kryvyi Rih", "Ukraine", 47.9086, 33.3433, 85.0, 2.0),
        PredefinedCity("Mykolaiv", "Ukraine", 46.9750, 31.9946, 52.0, 2.0),
        PredefinedCity("Mariupol", "Ukraine", 47.0956, 37.5498, 5.0, 2.0),
        PredefinedCity("Luhansk", "Ukraine", 48.5740, 39.3078, 104.0, 2.0),
        PredefinedCity("Vinnytsia", "Ukraine", 49.2331, 28.4682, 294.0, 2.0),
        PredefinedCity("Makiivka", "Ukraine", 48.0556, 37.9611, 169.0, 2.0),
        PredefinedCity("Simferopol", "Ukraine", 44.9521, 34.1024, 260.0, 3.0),
        PredefinedCity("Kherson", "Ukraine", 46.6354, 32.6169, 9.0, 2.0),
        PredefinedCity("Poltava", "Ukraine", 49.5883, 34.5514, 100.0, 2.0),
        PredefinedCity("Chernihiv", "Ukraine", 51.4982, 31.2893, 117.0, 2.0),
        PredefinedCity("Cherkasy", "Ukraine", 49.4285, 32.0621, 80.0, 2.0),
        PredefinedCity("Khmelnytskyi", "Ukraine", 49.4216, 26.9965, 299.0, 2.0),
        PredefinedCity("Zhytomyr", "Ukraine", 50.2649, 28.6587, 220.0, 2.0),
        PredefinedCity("Sumy", "Ukraine", 50.9077, 34.7981, 178.0, 2.0),
        PredefinedCity("Rivne", "Ukraine", 50.6199, 26.2516, 234.0, 2.0),
        PredefinedCity("Ivano-Frankivsk", "Ukraine", 48.9226, 24.7111, 277.0, 2.0),
        PredefinedCity("Ternopil", "Ukraine", 49.5535, 25.5948, 336.0, 2.0),
        PredefinedCity("Lutsk", "Ukraine", 50.7593, 25.3424, 220.0, 2.0),
        PredefinedCity("Bila Tserkva", "Ukraine", 49.7986, 30.1156, 155.0, 2.0),
        PredefinedCity("Kramatorsk", "Ukraine", 48.7232, 37.5564, 133.0, 2.0),
        PredefinedCity("Melitopol", "Ukraine", 46.8489, 35.3675, 20.0, 2.0),
        PredefinedCity("Uzhhorod", "Ukraine", 48.6208, 22.2879, 118.0, 2.0),
        PredefinedCity("Chernivtsi", "Ukraine", 48.2921, 25.9358, 248.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // SLOVACIA / SLOVAKIA (UTC+1) - 10 orașe
        // Bratislava (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Bratislava", "Slovakia", 48.1486, 17.1077, 134.0, 1.0),
        PredefinedCity("Košice", "Slovakia", 48.7164, 21.2611, 206.0, 1.0),
        PredefinedCity("Prešov", "Slovakia", 48.9986, 21.2391, 255.0, 1.0),
        PredefinedCity("Žilina", "Slovakia", 49.2231, 18.7394, 342.0, 1.0),
        PredefinedCity("Banská Bystrica", "Slovakia", 48.7395, 19.1532, 362.0, 1.0),
        PredefinedCity("Nitra", "Slovakia", 48.3069, 18.0864, 167.0, 1.0),
        PredefinedCity("Trnava", "Slovakia", 48.3774, 17.5883, 146.0, 1.0),
        PredefinedCity("Trenčín", "Slovakia", 48.8945, 18.0444, 211.0, 1.0),
        PredefinedCity("Martin", "Slovakia", 49.0636, 18.9214, 400.0, 1.0),
        PredefinedCity("Poprad", "Slovakia", 49.0600, 20.2975, 672.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // LITUANIA / LITHUANIA (UTC+2) - 10 orașe
        // Vilnius (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Vilnius", "Lithuania", 54.6872, 25.2797, 112.0, 2.0),
        PredefinedCity("Kaunas", "Lithuania", 54.8985, 23.9036, 73.0, 2.0),
        PredefinedCity("Klaipėda", "Lithuania", 55.7033, 21.1443, 21.0, 2.0),
        PredefinedCity("Šiauliai", "Lithuania", 55.9349, 23.3137, 106.0, 2.0),
        PredefinedCity("Panevėžys", "Lithuania", 55.7333, 24.3500, 60.0, 2.0),
        PredefinedCity("Alytus", "Lithuania", 54.3963, 24.0458, 85.0, 2.0),
        PredefinedCity("Marijampolė", "Lithuania", 54.5667, 23.3500, 71.0, 2.0),
        PredefinedCity("Mažeikiai", "Lithuania", 56.3167, 22.3333, 75.0, 2.0),
        PredefinedCity("Jonava", "Lithuania", 55.0833, 24.2833, 48.0, 2.0),
        PredefinedCity("Utena", "Lithuania", 55.4986, 25.6003, 116.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // LETONIA / LATVIA (UTC+2) - 10 orașe
        // Riga (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Riga", "Latvia", 56.9496, 24.1052, 7.0, 2.0),
        PredefinedCity("Daugavpils", "Latvia", 55.8833, 26.5333, 104.0, 2.0),
        PredefinedCity("Liepāja", "Latvia", 56.5047, 21.0108, 7.0, 2.0),
        PredefinedCity("Jelgava", "Latvia", 56.6528, 23.7131, 4.0, 2.0),
        PredefinedCity("Jūrmala", "Latvia", 56.9681, 23.7703, 15.0, 2.0),
        PredefinedCity("Ventspils", "Latvia", 57.3944, 21.5647, 8.0, 2.0),
        PredefinedCity("Rēzekne", "Latvia", 56.5000, 27.3167, 96.0, 2.0),
        PredefinedCity("Valmiera", "Latvia", 57.5389, 25.4264, 45.0, 2.0),
        PredefinedCity("Jēkabpils", "Latvia", 56.5000, 25.8667, 81.0, 2.0),
        PredefinedCity("Ogre", "Latvia", 56.8167, 24.6000, 32.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // ESTONIA (UTC+2) - 10 orașe
        // Tallinn (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Tallinn", "Estonia", 59.4370, 24.7536, 9.0, 2.0),
        PredefinedCity("Tartu", "Estonia", 58.3780, 26.7290, 59.0, 2.0),
        PredefinedCity("Narva", "Estonia", 59.3797, 28.1791, 37.0, 2.0),
        PredefinedCity("Pärnu", "Estonia", 58.3859, 24.4971, 6.0, 2.0),
        PredefinedCity("Kohtla-Järve", "Estonia", 59.3986, 27.2731, 60.0, 2.0),
        PredefinedCity("Viljandi", "Estonia", 58.3639, 25.5900, 87.0, 2.0),
        PredefinedCity("Rakvere", "Estonia", 59.3469, 26.3556, 81.0, 2.0),
        PredefinedCity("Maardu", "Estonia", 59.4767, 25.0161, 30.0, 2.0),
        PredefinedCity("Sillamäe", "Estonia", 59.3947, 27.7656, 25.0, 2.0),
        PredefinedCity("Kuressaare", "Estonia", 58.2483, 22.5039, 5.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // MOLDOVA (UTC+2) - 10 orașe
        // Chișinău (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Chișinău", "Moldova", 47.0105, 28.8638, 85.0, 2.0),
        PredefinedCity("Tiraspol", "Moldova", 46.8403, 29.6433, 43.0, 2.0),
        PredefinedCity("Bălți", "Moldova", 47.7617, 27.9289, 102.0, 2.0),
        PredefinedCity("Bender", "Moldova", 46.8328, 29.4714, 50.0, 2.0),
        PredefinedCity("Rîbnița", "Moldova", 47.7667, 29.0000, 55.0, 2.0),
        PredefinedCity("Cahul", "Moldova", 45.9042, 28.1944, 117.0, 2.0),
        PredefinedCity("Ungheni", "Moldova", 47.2100, 27.8000, 66.0, 2.0),
        PredefinedCity("Soroca", "Moldova", 48.1667, 28.3000, 52.0, 2.0),
        PredefinedCity("Orhei", "Moldova", 47.3833, 28.8167, 130.0, 2.0),
        PredefinedCity("Dubăsari", "Moldova", 47.2656, 29.1622, 40.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // SLOVENIA (UTC+1) - 10 orașe
        // Ljubljana (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Ljubljana", "Slovenia", 46.0569, 14.5058, 295.0, 1.0),
        PredefinedCity("Maribor", "Slovenia", 46.5547, 15.6459, 275.0, 1.0),
        PredefinedCity("Celje", "Slovenia", 46.2361, 15.2678, 241.0, 1.0),
        PredefinedCity("Kranj", "Slovenia", 46.2389, 14.3556, 386.0, 1.0),
        PredefinedCity("Velenje", "Slovenia", 46.3594, 15.1103, 410.0, 1.0),
        PredefinedCity("Koper", "Slovenia", 45.5469, 13.7294, 5.0, 1.0),
        PredefinedCity("Novo Mesto", "Slovenia", 45.8069, 15.1611, 220.0, 1.0),
        PredefinedCity("Ptuj", "Slovenia", 46.4200, 15.8700, 225.0, 1.0),
        PredefinedCity("Trbovlje", "Slovenia", 46.1500, 15.0500, 270.0, 1.0),
        PredefinedCity("Bled", "Slovenia", 46.3683, 14.1147, 501.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // BOSNIA ȘI HERȚEGOVINA (UTC+1) - 10 orașe
        // Sarajevo (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Sarajevo", "Bosnia and Herzegovina", 43.8563, 18.4131, 500.0, 1.0),
        PredefinedCity("Banja Luka", "Bosnia and Herzegovina", 44.7758, 17.1858, 163.0, 1.0),
        PredefinedCity("Tuzla", "Bosnia and Herzegovina", 44.5381, 18.6761, 237.0, 1.0),
        PredefinedCity("Zenica", "Bosnia and Herzegovina", 44.2017, 17.9078, 314.0, 1.0),
        PredefinedCity("Mostar", "Bosnia and Herzegovina", 43.3438, 17.8078, 60.0, 1.0),
        PredefinedCity("Bijeljina", "Bosnia and Herzegovina", 44.7567, 19.2142, 82.0, 1.0),
        PredefinedCity("Brčko", "Bosnia and Herzegovina", 44.8728, 18.8097, 95.0, 1.0),
        PredefinedCity("Bihać", "Bosnia and Herzegovina", 44.8167, 15.8700, 245.0, 1.0),
        PredefinedCity("Prijedor", "Bosnia and Herzegovina", 44.9797, 16.7136, 133.0, 1.0),
        PredefinedCity("Trebinje", "Bosnia and Herzegovina", 42.7117, 18.3439, 273.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // MACEDONIA DE NORD (UTC+1) - 10 orașe
        // Skopje (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Skopje", "North Macedonia", 41.9973, 21.4280, 245.0, 1.0),
        PredefinedCity("Bitola", "North Macedonia", 41.0311, 21.3403, 615.0, 1.0),
        PredefinedCity("Kumanovo", "North Macedonia", 42.1322, 21.7144, 340.0, 1.0),
        PredefinedCity("Prilep", "North Macedonia", 41.3464, 21.5542, 622.0, 1.0),
        PredefinedCity("Tetovo", "North Macedonia", 42.0097, 20.9714, 468.0, 1.0),
        PredefinedCity("Veles", "North Macedonia", 41.7156, 21.7753, 200.0, 1.0),
        PredefinedCity("Ohrid", "North Macedonia", 41.1231, 20.8016, 695.0, 1.0),
        PredefinedCity("Gostivar", "North Macedonia", 41.7958, 20.9083, 515.0, 1.0),
        PredefinedCity("Štip", "North Macedonia", 41.7458, 22.1903, 305.0, 1.0),
        PredefinedCity("Strumica", "North Macedonia", 41.4375, 22.6431, 260.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // ALBANIA (UTC+1) - 10 orașe
        // Tirana (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Tirana", "Albania", 41.3275, 19.8187, 110.0, 1.0),
        PredefinedCity("Durrës", "Albania", 41.3246, 19.4565, 5.0, 1.0),
        PredefinedCity("Vlorë", "Albania", 40.4667, 19.4900, 2.0, 1.0),
        PredefinedCity("Elbasan", "Albania", 41.1125, 20.0822, 150.0, 1.0),
        PredefinedCity("Shkodër", "Albania", 42.0683, 19.5126, 13.0, 1.0),
        PredefinedCity("Korçë", "Albania", 40.6186, 20.7808, 869.0, 1.0),
        PredefinedCity("Fier", "Albania", 40.7239, 19.5567, 11.0, 1.0),
        PredefinedCity("Berat", "Albania", 40.7058, 19.9522, 59.0, 1.0),
        PredefinedCity("Lushnjë", "Albania", 40.9419, 19.7050, 19.0, 1.0),
        PredefinedCity("Sarandë", "Albania", 39.8661, 20.0050, 3.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // MUNTENEGRU / MONTENEGRO (UTC+1) - 10 orașe
        // Podgorica (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Podgorica", "Montenegro", 42.4304, 19.2594, 44.0, 1.0),
        PredefinedCity("Nikšić", "Montenegro", 42.7731, 18.9444, 630.0, 1.0),
        PredefinedCity("Bijelo Polje", "Montenegro", 43.0386, 19.7483, 592.0, 1.0),
        PredefinedCity("Cetinje", "Montenegro", 42.3931, 18.9236, 671.0, 1.0),
        PredefinedCity("Bar", "Montenegro", 42.0936, 19.1003, 5.0, 1.0),
        PredefinedCity("Herceg Novi", "Montenegro", 42.4531, 18.5375, 10.0, 1.0),
        PredefinedCity("Berane", "Montenegro", 42.8442, 19.8625, 693.0, 1.0),
        PredefinedCity("Budva", "Montenegro", 42.2911, 18.8403, 2.0, 1.0),
        PredefinedCity("Ulcinj", "Montenegro", 41.9297, 19.2086, 5.0, 1.0),
        PredefinedCity("Kotor", "Montenegro", 42.4247, 18.7711, 5.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // KOSOVO (UTC+1) - 5 orașe
        // Pristina (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Pristina", "Kosovo", 42.6629, 21.1655, 595.0, 1.0),
        PredefinedCity("Prizren", "Kosovo", 42.2139, 20.7397, 412.0, 1.0),
        PredefinedCity("Ferizaj", "Kosovo", 42.3706, 21.1553, 578.0, 1.0),
        PredefinedCity("Peja", "Kosovo", 42.6592, 20.2886, 498.0, 1.0),
        PredefinedCity("Gjakova", "Kosovo", 42.3803, 20.4308, 378.0, 1.0),

        // ══════════════════════════════════════════════════════════
        // CIPRU / CYPRUS (UTC+2) - 5 orașe
        // Nicosia (capitala), apoi descrescător după populație
        // ══════════════════════════════════════════════════════════
        PredefinedCity("Nicosia", "Cyprus", 35.1856, 33.3823, 220.0, 2.0),
        PredefinedCity("Limassol", "Cyprus", 34.6841, 33.0379, 8.0, 2.0),
        PredefinedCity("Larnaca", "Cyprus", 34.9229, 33.6233, 1.0, 2.0),
        PredefinedCity("Paphos", "Cyprus", 34.7754, 32.4245, 72.0, 2.0),
        PredefinedCity("Famagusta", "Cyprus", 35.1174, 33.9420, 5.0, 2.0),

        // ══════════════════════════════════════════════════════════
        // STATE MICI EUROPENE
        // ══════════════════════════════════════════════════════════
        
        // ISLANDA (UTC+0) - 3 orașe
        PredefinedCity("Reykjavik", "Iceland", 64.1466, -21.9426, 37.0, 0.0),
        PredefinedCity("Kópavogur", "Iceland", 64.1101, -21.9131, 52.0, 0.0),
        PredefinedCity("Akureyri", "Iceland", 65.6885, -18.1262, 23.0, 0.0),

        // MALTA (UTC+1) - 3 orașe
        PredefinedCity("Valletta", "Malta", 35.8989, 14.5146, 56.0, 1.0),
        PredefinedCity("Birkirkara", "Malta", 35.8964, 14.4631, 75.0, 1.0),
        PredefinedCity("Sliema", "Malta", 35.9122, 14.5028, 10.0, 1.0),

        // MONACO (UTC+1) - 1 oraș
        PredefinedCity("Monaco", "Monaco", 43.7384, 7.4246, 62.0, 1.0),

        // LIECHTENSTEIN (UTC+1) - 1 oraș
        PredefinedCity("Vaduz", "Liechtenstein", 47.1410, 9.5215, 455.0, 1.0),

        // SAN MARINO (UTC+1) - 1 oraș
        PredefinedCity("San Marino", "San Marino", 43.9424, 12.4578, 675.0, 1.0),

        // ANDORRA (UTC+1) - 1 oraș
        PredefinedCity("Andorra la Vella", "Andorra", 42.5063, 1.5218, 1023.0, 1.0),

        // VATICAN (UTC+1) - 1 oraș
        PredefinedCity("Vatican City", "Vatican", 41.9029, 12.4534, 75.0, 1.0),

        // LUXEMBOURG (UTC+1) - 2 orașe
        PredefinedCity("Luxembourg City", "Luxembourg", 49.6117, 6.1319, 302.0, 1.0),
        PredefinedCity("Esch-sur-Alzette", "Luxembourg", 49.4958, 5.9806, 280.0, 1.0),

        // BELARUS (UTC+3) - 5 orașe
        PredefinedCity("Minsk", "Belarus", 53.9006, 27.5590, 220.0, 3.0),
        PredefinedCity("Gomel", "Belarus", 52.4345, 30.9754, 115.0, 3.0),
        PredefinedCity("Mogilev", "Belarus", 53.9045, 30.3449, 187.0, 3.0),
        PredefinedCity("Vitebsk", "Belarus", 55.1904, 30.2049, 176.0, 3.0),
        PredefinedCity("Grodno", "Belarus", 53.6884, 23.8258, 139.0, 3.0),
		
		
		// USA (orașe mari)
        PredefinedCity("New York", "USA", 40.7128, -74.0060, 10.0, -5.0),
        PredefinedCity("Los Angeles", "USA", 34.0522, -118.2437, 71.0, -8.0),
        PredefinedCity("Chicago", "USA", 41.8781, -87.6298, 181.0, -6.0),
        PredefinedCity("Houston", "USA", 29.7604, -95.3698, 15.0, -6.0),
        PredefinedCity("Phoenix", "USA", 33.4484, -112.0740, 340.0, -7.0),
        PredefinedCity("San Francisco", "USA", 37.7749, -122.4194, 16.0, -8.0),
        PredefinedCity("Seattle", "USA", 47.6062, -122.3321, 56.0, -8.0),
        PredefinedCity("Miami", "USA", 25.7617, -80.1918, 2.0, -5.0),
        PredefinedCity("Boston", "USA", 42.3601, -71.0589, 43.0, -5.0),
        PredefinedCity("Las Vegas", "USA", 36.1699, -115.1398, 610.0, -8.0),
        PredefinedCity("Denver", "USA", 39.7392, -104.9903, 1609.0, -7.0),
        PredefinedCity("Atlanta", "USA", 33.7490, -84.3880, 320.0, -5.0)
        
        // TODO: Adaugă aici restul de ~1000 orașe din Europa
        // Poți adăuga manual în acest format:
        // PredefinedCity("NumeOras", "Tara", latitude, longitude, altitude, timeZone),
    )
}