package com.sarvodhaya.store.data.seed

import com.sarvodhaya.store.domain.model.Banner
import com.sarvodhaya.store.domain.model.Category
import com.sarvodhaya.store.domain.model.Product

/** Bundled catalog used until Firestore has content. */
object DemoCatalog {

    val categories = listOf(
        Category("paints", "Paints & Finishes", "Emulsions, primers & enamels", "format_paint"),
        Category("locks", "Door Locks", "Mortise, padlocks & handles", "lock"),
        Category("cement", "Cement", "OPC, PPC & ready mix", "inventory_2"),
        Category("steel", "Steel & TMT", "TMT bars, angles & mesh", "construction"),
        Category("plumbing", "Plumbing", "Pipes, taps & fittings", "plumbing"),
        Category("electrical", "Electrical", "Wires, switches & lighting", "electrical_services"),
        Category("tools", "Tools", "Power tools & hand tools", "handyman"),
        Category("adhesives", "Adhesives", "Fevicol, sealants & repair", "science")
    )

    val banners = listOf(
        Banner(
            id = "banner-paints",
            title = "Monsoon Makeover Sale",
            subtitle = "Up to 25% off on exterior emulsions",
            ctaLabel = "Shop paints",
            categoryId = "paints",
            startColor = 0xFF3C6157,
            endColor = 0xFF223B36
        ),
        Banner(
            id = "banner-locks",
            title = "Secure Every Door",
            subtitle = "Premium locks and handles from Rs.399",
            ctaLabel = "Explore locks",
            categoryId = "locks",
            startColor = 0xFF4E6E65,
            endColor = 0xFF2C4A43
        ),
        Banner(
            id = "banner-cement",
            title = "Bulk Cement Deals",
            subtitle = "Trusted brands delivered to your site",
            ctaLabel = "Order cement",
            categoryId = "cement",
            startColor = 0xFF6A7F77,
            endColor = 0xFF3C4F49
        ),
        Banner(
            id = "banner-tools",
            title = "Power Tool Fest",
            subtitle = "Drills, grinders and saws at best prices",
            ctaLabel = "View tools",
            categoryId = "tools",
            startColor = 0xFF587A6F,
            endColor = 0xFF2A443C
        )
    )

    private fun product(
        id: String,
        name: String,
        brand: String,
        categoryId: String,
        price: Double,
        mrp: Double,
        unit: String,
        stock: Int,
        rating: Double,
        description: String,
        specs: Map<String, String>
    ) = product(id, name, brand, categoryId, price, mrp, unit, stock, rating, false, description, specs)

    private fun product(
        id: String,
        name: String,
        brand: String,
        categoryId: String,
        price: Double,
        mrp: Double,
        unit: String,
        stock: Int,
        rating: Double,
        featured: Boolean = false,
        description: String,
        specs: Map<String, String>
    ) = Product(
        id = id,
        name = name,
        brand = brand,
        categoryId = categoryId,
        description = description,
        specifications = specs,
        price = price,
        mrp = mrp,
        unit = unit,
        stock = stock,
        rating = rating,
        featured = featured
    )

    val products: List<Product> = listOf(
        // ---------- Paints ----------
        product(
            "pnt-apcolite-20l", "Apcolite Premium Emulsion", "Asian Paints", "paints",
            6450.0, 7200.0, "20 L bucket", 18, 4.6, true,
            "Smooth, washable interior emulsion with excellent coverage and a low-odour finish.",
            mapOf("Finish" to "Matt", "Coverage" to "1800 sq ft", "Drying time" to "30 minutes")
        ),
        product(
            "pnt-weathercoat-20l", "WeatherCoat Long Life Exterior", "Berger", "paints",
            5890.0, 6990.0, "20 L bucket", 12, 4.5, true,
            "Weather-resistant exterior emulsion that protects walls from rain, sun and dust.",
            mapOf("Finish" to "Sheen", "Coverage" to "1600 sq ft", "Warranty" to "7 years")
        ),
        product(
            "pnt-decoprime-20l", "Decoprime Wall Primer", "Asian Paints", "paints",
            2280.0, 2600.0, "20 L bucket", 25, 4.4,
            "Water-based primer that seals porous plaster and improves topcoat adhesion.",
            mapOf("Type" to "Water based", "Coverage" to "2000 sq ft", "Dry time" to "1 hour")
        ),
        product(
            "pnt-excel-mica-10l", "Excel Mica Marble Emulsion", "Nerolac", "paints",
            4120.0, 4700.0, "10 L bucket", 9, 4.3,
            "Designer interior emulsion with a subtle mica sheen for living rooms.",
            mapOf("Finish" to "Mica sheen", "Coverage" to "1200 sq ft", "Base" to "Water based")
        ),

        // ---------- Locks ----------
        product(
            "lck-godrej-ultra", "Ultra Door Lock", "Godrej", "locks",
            1450.0, 1799.0, "1 lock", 30, 4.7, true,
            "Heavy duty mortise lock with 7 levers, brass finish and anti-theft deadbolt.",
            mapOf("Levers" to "7", "Material" to "Brass", "Finish" to "Satin nickel")
        ),
        product(
            "lck-dorset-handle", "Brass Mortise Handle Set", "Dorset", "locks",
            1180.0, 1400.0, "1 set", 22, 4.5,
            "Solid brass handle set with both-side keys and a smooth latch mechanism.",
            mapOf("Material" to "Brass", "Suitable for" to "35-50 mm doors", "Keys" to "3")
        ),
        product(
            "lck-yale-smart", "Smart Digital Door Lock", "Yale", "locks",
            8999.0, 10999.0, "1 unit", 6, 4.6,
            "Keypad and fingerprint entry with tamper alarm and mechanical key override.",
            mapOf("Access" to "PIN, fingerprint, key", "Battery" to "4 x AA", "Users" to "50")
        ),
        product(
            "lck-navtal-padlock", "Nav-Tal 7 Lever Padlock", "Godrej", "locks",
            399.0, 520.0, "1 padlock", 48, 4.4,
            "Rust-resistant padlock with hardened shackle for gates and shutters.",
            mapOf("Levers" to "7", "Finish" to "Electroplated", "Shackle" to "Hardened steel")
        ),

        // ---------- Cement ----------
        product(
            "cem-ultratech-53", "UltraTech OPC 53 Grade", "UltraTech", "cement",
            430.0, 480.0, "50 kg bag", 120, 4.8, true,
            "High strength ordinary portland cement for RCC structures and heavy slabs.",
            mapOf("Grade" to "OPC 53", "Setting time" to "30-600 min", "Weight" to "50 kg")
        ),
        product(
            "cem-acc-suraksha", "ACC Suraksha PPC", "ACC", "cement",
            405.0, 450.0, "50 kg bag", 90, 4.6,
            "Portland pozzolana cement with better water resistance and low heat of hydration.",
            mapOf("Grade" to "PPC", "Setting time" to "30-600 min", "Weight" to "50 kg")
        ),
        product(
            "cem-ambuja-cool", "Plus Cool Walls PPC", "Ambuja", "cement",
            418.0, 465.0, "50 kg bag", 75, 4.5,
            "Reduces surface temperature by up to 5 degrees, ideal for external plaster.",
            mapOf("Grade" to "PPC", "Benefit" to "Heat resistant", "Weight" to "50 kg")
        ),
        product(
            "cem-birla-a1", "A1 Premium PPC", "Birla", "cement",
            398.0, 440.0, "50 kg bag", 110, 4.4,
            "Fine-ground PPC for smooth plastering and masonry work.",
            mapOf("Grade" to "PPC", "Fineness" to "High", "Weight" to "50 kg")
        ),

        // ---------- Steel ----------
        product(
            "stl-tiscon-12mm", "Tiscon TMT Bar 12 mm", "Tata", "steel",
            765.0, 840.0, "per rod (12 m)", 300, 4.8, true,
            "Fe 550D TMT bar with high ductility for beams, columns and slabs.",
            mapOf("Grade" to "Fe 550D", "Diameter" to "12 mm", "Length" to "12 m")
        ),
        product(
            "stl-jsw-10mm", "Neosteel TMT Bar 10 mm", "JSW", "steel",
            532.0, 585.0, "per rod (12 m)", 260, 4.6,
            "Corrosion resistant TMT bar with excellent bendability.",
            mapOf("Grade" to "Fe 550D", "Diameter" to "10 mm", "Length" to "12 m")
        ),
        product(
            "stl-sail-8mm", "TMT Bar 8 mm", "SAIL", "steel",
            341.0, 380.0, "per rod (12 m)", 210, 4.4,
            "Economical reinforcement bar for stirrups and light structural work.",
            mapOf("Grade" to "Fe 500D", "Diameter" to "8 mm", "Length" to "12 m")
        ),
        product(
            "stl-kamdhenu-16mm", "TMT Bar 16 mm", "Kamdhenu", "steel",
            1360.0, 1490.0, "per rod (12 m)", 140, 4.3,
            "Heavy gauge TMT bar for foundations and high load structures.",
            mapOf("Grade" to "Fe 500D", "Diameter" to "16 mm", "Length" to "12 m")
        ),

        // ---------- Plumbing ----------
        product(
            "plm-astral-cpvc-1in", "CPVC Pipe 1 inch", "Astral", "plumbing",
            465.0, 540.0, "3 m length", 85, 4.6, true,
            "Hot and cold water CPVC pipe rated for 93 C, lead free and durable.",
            mapOf("Size" to "1 inch", "Length" to "3 m", "Temp rating" to "93 C")
        ),
        product(
            "plm-supreme-solvent", "PVC Solvent Cement 100 ml", "Supreme", "plumbing",
            135.0, 165.0, "100 ml tin", 160, 4.5,
            "Fast setting solvent cement for strong PVC pipe joints.",
            mapOf("Volume" to "100 ml", "Setting time" to "15 minutes", "Colour" to "Clear")
        ),
        product(
            "plm-hindware-mixer", "Wall Mixer Tap", "Hindware", "plumbing",
            2240.0, 2890.0, "1 piece", 24, 4.4,
            "Chrome plated brass wall mixer with ceramic cartridge and 3-year warranty.",
            mapOf("Material" to "Brass", "Finish" to "Chrome", "Warranty" to "3 years")
        ),
        product(
            "plm-pvc-elbow", "PVC Elbow 1 inch (Pack of 10)", "Supreme", "plumbing",
            210.0, 250.0, "pack of 10", 130, 4.3,
            "Schedule 40 elbow fittings for durable 90 degree pipe bends.",
            mapOf("Size" to "1 inch", "Quantity" to "10 pieces", "Schedule" to "40")
        ),

        // ---------- Electrical ----------
        product(
            "elc-havells-wire", "FR Wire 1.5 sq mm (90 m)", "Havells", "electrical",
            1680.0, 1950.0, "90 m coil", 60, 4.7, true,
            "Flame retardant copper wire for lighting and power circuits.",
            mapOf("Size" to "1.5 sq mm", "Length" to "90 m", "Type" to "FR PVC")
        ),
        product(
            "elc-anchor-switch", "Roma Modular Switch", "Anchor", "electrical",
            78.0, 95.0, "1 piece", 300, 4.4,
            "Smooth rocker switch with silver contacts and a matte finish.",
            mapOf("Rating" to "6 A", "Type" to "1 way", "Warranty" to "2 years")
        ),
        product(
            "elc-philips-bulb", "9W LED Bulb (Pack of 4)", "Philips", "electrical",
            420.0, 560.0, "pack of 4", 95, 4.6,
            "Cool daylight LED bulbs with wide voltage range and 2-year replacement.",
            mapOf("Wattage" to "9 W", "Colour" to "Cool daylight", "Lumens" to "825 lm")
        ),
        product(
            "elc-legrand-mcb", "6A Single Pole MCB", "Legrand", "electrical",
            235.0, 290.0, "1 piece", 140, 4.5,
            "Miniature circuit breaker with C-curve protection against overload and short circuit.",
            mapOf("Rating" to "6 A", "Poles" to "1", "Curve" to "C")
        ),

        // ---------- Tools ----------
        product(
            "tol-bosch-gsb550", "GSB 550 Impact Drill", "Bosch", "tools",
            3199.0, 3999.0, "1 drill", 14, 4.8, true,
            "550 W hammer drill with variable speed trigger, for concrete and wood.",
            mapOf("Power" to "550 W", "Chuck" to "13 mm", "Warranty" to "1 year")
        ),
        product(
            "tol-stanley-screwdriver", "26-Piece Screwdriver Set", "Stanley", "tools",
            890.0, 1150.0, "1 set", 32, 4.5,
            "Chrome vanadium bits with magnetic tips and a blow-moulded case.",
            mapOf("Pieces" to "26", "Material" to "Chrome vanadium", "Case" to "Yes")
        ),
        product(
            "tol-makita-grinder", "Angle Grinder 4 inch", "Makita", "tools",
            4299.0, 5200.0, "1 grinder", 8, 4.6,
            "850 W grinder with tool-free guard for cutting and grinding metal.",
            mapOf("Power" to "850 W", "Disc size" to "100 mm", "No load speed" to "11000 rpm")
        ),
        product(
            "tol-taparia-wrench", "8 inch Adjustable Wrench", "Taparia", "tools",
            345.0, 420.0, "1 piece", 55, 4.4,
            "Forged steel wrench with a calibrated jaw and comfort grip.",
            mapOf("Size" to "8 inch", "Material" to "Forged steel", "Finish" to "Nickel")
        ),

        // ---------- Adhesives ----------
        product(
            "adh-fevicol-sh", "Fevicol SH 1 kg", "Pidilite", "adhesives",
            395.0, 450.0, "1 kg jar", 88, 4.8, true,
            "Trusted woodworking adhesive for furniture and plywood joints.",
            mapOf("Weight" to "1 kg", "Type" to "Synthetic resin", "Shelf life" to "18 months")
        ),
        product(
            "adh-drfixit-lw", "LW+ Waterproofing 5 L", "Dr. Fixit", "adhesives",
            520.0, 640.0, "5 L can", 40, 4.5,
            "Elastomeric waterproofing compound for terraces and bathrooms.",
            mapOf("Volume" to "5 L", "Coverage" to "50 sq ft", "Base" to "Acrylic")
        ),
        product(
            "adh-araldite", "Standard Epoxy 36 g", "Araldite", "adhesives",
            210.0, 240.0, "36 g pack", 70, 4.6,
            "Two-part epoxy resin that bonds metal, ceramic, wood and glass.",
            mapOf("Weight" to "36 g", "Set time" to "30 minutes", "Strength" to "High")
        ),
        product(
            "adh-loctite-glue", "Super Glue 20 g", "Loctite", "adhesives",
            125.0, 150.0, "20 g bottle", 150, 4.4,
            "Instant cyanoacrylate adhesive with precise nozzle control.",
            mapOf("Weight" to "20 g", "Set time" to "10 seconds", "Type" to "Cyanoacrylate")
        )
    )
}
