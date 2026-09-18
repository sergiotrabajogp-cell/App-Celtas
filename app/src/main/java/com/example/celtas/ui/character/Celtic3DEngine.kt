package com.example.celtas.ui.character

import androidx.compose.ui.graphics.Color
import com.example.celtas.model.AccessorySlot
import com.example.celtas.model.CelticAccessory
import com.example.celtas.model.CharacterCustomization
import kotlin.math.cos
import kotlin.math.sin

data class Vector3D(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vector3D) = Vector3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3D) = Vector3D(x - other.x, y - other.y, z - other.z)
    operator fun times(scale: Float) = Vector3D(x * scale, y * scale, z * scale)

    fun rotateY(angleRad: Float): Vector3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vector3D(
            x = x * cosA + z * sinA,
            y = y,
            z = -x * sinA + z * cosA
        )
    }

    fun rotateX(angleRad: Float): Vector3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Vector3D(
            x = x,
            y = y * cosA - z * sinA,
            z = y * sinA + z * cosA
        )
    }

    fun cross(other: Vector3D): Vector3D {
        return Vector3D(
            x = y * other.z - z * other.y,
            y = z * other.x - x * other.z,
            z = x * other.y - y * other.x
        )
    }

    fun dot(other: Vector3D): Float = x * other.x + y * other.y + z * other.z

    fun normalized(): Vector3D {
        val length = kotlin.math.sqrt(x * x + y * y + z * z)
        return if (length > 0.0001f) Vector3D(x / length, y / length, z / length) else this
    }
}

data class Polygon3D(
    val vertices: List<Vector3D>,
    val baseColor: Color,
    val outlineColor: Color? = null,
    val strokeWidth: Float = 1.5f
)

data class ProjectedPolygon(
    val points: List<Pair<Float, Float>>,
    val shadedColor: Color,
    val outlineColor: Color?,
    val strokeWidth: Float,
    val averageZ: Float
)

object Celtic3DEngine {

    private val LIGHT_DIR = Vector3D(0.4f, -0.8f, 0.5f).normalized()

    fun projectAndSort(
        polygons: List<Polygon3D>,
        yawRad: Float,
        pitchRad: Float,
        viewportWidth: Float,
        viewportHeight: Float,
        zoom: Float = 1.0f
    ): List<ProjectedPolygon> {
        val centerScreenX = viewportWidth / 2f
        val centerScreenY = viewportHeight / 2f + 25f
        val fov = 420f * zoom
        val cameraDistance = 340f

        val projectedList = mutableListOf<ProjectedPolygon>()

        for (poly in polygons) {
            // Rotate all vertices by yaw then pitch
            val transformedVertices = poly.vertices.map { v ->
                v.rotateY(yawRad).rotateX(pitchRad)
            }

            // Normal calculation for directional lighting
            var normal = Vector3D(0f, 0f, 1f)
            if (transformedVertices.size >= 3) {
                val v0 = transformedVertices[0]
                val v1 = transformedVertices[1]
                val v2 = transformedVertices[2]
                val edge1 = v1 - v0
                val edge2 = v2 - v0
                normal = edge1.cross(edge2).normalized()
            }

            // Simple backface culling / directional shade factor
            val lightFactor = (normal.dot(LIGHT_DIR) * 0.45f + 0.65f).coerceIn(0.28f, 1.05f)
            // Specular reflection (Phong component) for metallic surfaces (bronze, gold, steel)
            val viewDir = Vector3D(0f, 0f, 1f)
            val halfVector = (LIGHT_DIR + viewDir).normalized()
            val specular = (normal.dot(halfVector)).coerceAtLeast(0f)
            val isMetallic = (poly.baseColor.red > 0.55f && poly.baseColor.green > 0.35f && poly.baseColor.blue < 0.4f) ||
                             (poly.baseColor.red > 0.8f && poly.baseColor.green > 0.8f && poly.baseColor.blue > 0.8f)
            val specFactor = if (isMetallic) (specular * specular * specular * specular) * 0.32f else 0f

            val shaded = Color(
                red = (poly.baseColor.red * lightFactor + specFactor).coerceIn(0f, 1f),
                green = (poly.baseColor.green * lightFactor + specFactor * 0.85f).coerceIn(0f, 1f),
                blue = (poly.baseColor.blue * lightFactor + specFactor * 0.4f).coerceIn(0f, 1f),
                alpha = poly.baseColor.alpha
            )

            var sumZ = 0f
            val screenPoints = transformedVertices.map { v ->
                val zEff = (v.z + cameraDistance).coerceAtLeast(10f)
                sumZ += v.z
                val projX = centerScreenX + (v.x * fov) / zEff
                val projY = centerScreenY + (v.y * fov) / zEff
                Pair(projX, projY)
            }

            val avgZ = sumZ / transformedVertices.size
            projectedList.add(
                ProjectedPolygon(
                    points = screenPoints,
                    shadedColor = shaded,
                    outlineColor = poly.outlineColor,
                    strokeWidth = poly.strokeWidth,
                    averageZ = avgZ
                )
            )
        }

        // Painter's algorithm: sort by depth (farthest Z first, closest last)
        return projectedList.sortedBy { it.averageZ }
    }

    // --- Mesh Generation Helpers ---
    fun createBox(
        cx: Float, cy: Float, cz: Float,
        w: Float, h: Float, d: Float,
        color: Color,
        outline: Color? = Color(0x33000000)
    ): List<Polygon3D> {
        val hw = w / 2f
        val hh = h / 2f
        val hd = d / 2f

        val v0 = Vector3D(cx - hw, cy - hh, cz - hd)
        val v1 = Vector3D(cx + hw, cy - hh, cz - hd)
        val v2 = Vector3D(cx + hw, cy + hh, cz - hd)
        val v3 = Vector3D(cx - hw, cy + hh, cz - hd)
        val v4 = Vector3D(cx - hw, cy - hh, cz + hd)
        val v5 = Vector3D(cx + hw, cy - hh, cz + hd)
        val v6 = Vector3D(cx + hw, cy + hh, cz + hd)
        val v7 = Vector3D(cx - hw, cy + hh, cz + hd)

        return listOf(
            // Front (Z+)
            Polygon3D(listOf(v4, v5, v6, v7), color, outline),
            // Back (Z-)
            Polygon3D(listOf(v1, v0, v3, v2), color * 0.75f, outline),
            // Top (Y-)
            Polygon3D(listOf(v0, v1, v5, v4), color * 1.15f, outline),
            // Bottom (Y+)
            Polygon3D(listOf(v3, v7, v6, v2), color * 0.6f, outline),
            // Left (X-)
            Polygon3D(listOf(v0, v4, v7, v3), color * 0.85f, outline),
            // Right (X+)
            Polygon3D(listOf(v5, v1, v2, v6), color * 0.95f, outline)
        )
    }

    private operator fun Color.times(factor: Float): Color {
        return Color(
            red = (red * factor).coerceIn(0f, 1f),
            green = (green * factor).coerceIn(0f, 1f),
            blue = (blue * factor).coerceIn(0f, 1f),
            alpha = alpha
        )
    }

    // --- Build Full Celtic Character Model ---
    fun buildCelticCharacter(
        customization: CharacterCustomization,
        accessories: List<CelticAccessory>,
        time: Float = 0f
    ): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()

        // Colors based on customization
        val skinColor = when (customization.skinToneIndex) {
            1 -> Color(0xFFD6A284) // Brezo Atlántico bronceado
            2 -> Color(0xFFC68A60) // Dorado solar
            else -> Color(0xFFF2D1B3) // Pálido Celta
        }

        val hairColor = when (customization.hairColorIndex) {
            1 -> Color(0xFFF1C40F) // Rubio Dorado
            2 -> Color(0xFF6E401F) // Castaño Roble
            3 -> Color(0xFF1E272C) // Negro Cuervo
            else -> Color(0xFFD35400) // Pelirrojo Gaélico
        }

        val tunicColor = when (customization.archetype) {
            "druida" -> Color(0xFF2E7D32) // Verde Druídico bosque
            "guerrero" -> Color(0xFF8B2500) // Terracota Guerrero
            "exploradora" -> Color(0xFF00695C) // Verde azulado cazadora
            else -> Color(0xFFB45309) // Bardo ámbar
        }

        val trousersColor = Color(0xFF374151) // Braccae grisáceo oscuro
        val bootColor = Color(0xFF422006) // Cuero rústico
        val beltGold = Color(0xFFD97706) // Hebilla dorada

        // Breathing idle animation
        val breath = sin(time * 3f) * 1.5f

        // 1. Stone Altar Pedestal (circular stone dais at feet)
        val stoneBaseY = 95f
        val segments = 12
        val radiusOuter = 65f
        val radiusInner = 55f
        val stoneHeight = 12f
        val stoneColor = Color(0xFF475569)
        val stoneTopColor = Color(0xFF64748B)

        for (i in 0 until segments) {
            val a1 = (i.toFloat() / segments) * 2f * Math.PI.toFloat()
            val a2 = ((i + 1).toFloat() / segments) * 2f * Math.PI.toFloat()

            val p1Top = Vector3D(cos(a1) * radiusOuter, stoneBaseY, sin(a1) * radiusOuter)
            val p2Top = Vector3D(cos(a2) * radiusOuter, stoneBaseY, sin(a2) * radiusOuter)
            val p1Bot = Vector3D(cos(a1) * radiusOuter, stoneBaseY + stoneHeight, sin(a1) * radiusOuter)
            val p2Bot = Vector3D(cos(a2) * radiusOuter, stoneBaseY + stoneHeight, sin(a2) * radiusOuter)

            // Side wall
            polys.add(Polygon3D(listOf(p1Top, p2Top, p2Bot, p1Bot), stoneColor, Color(0xFF1E293B)))
            // Top rim wedge
            val centerTop = Vector3D(0f, stoneBaseY, 0f)
            polys.add(Polygon3D(listOf(centerTop, p1Top, p2Top), stoneTopColor, Color(0xFF334155)))
        }

        // 2. Boots & Feet
        val leftFootX = -12f
        val rightFootX = 12f
        val footY = 86f
        polys.addAll(createBox(leftFootX, footY, 2f, 11f, 16f, 15f, bootColor))
        polys.addAll(createBox(rightFootX, footY, 2f, 11f, 16f, 15f, bootColor))

        // 3. Legs (Trousers / Braccae)
        val legY = 66f
        polys.addAll(createBox(leftFootX, legY, 0f, 11f, 26f, 12f, trousersColor))
        polys.addAll(createBox(rightFootX, legY, 0f, 11f, 26f, 12f, trousersColor))

        // 4. Torso & Celtic Tunic (with breath bobbing)
        val torsoY = 32f + breath
        polys.addAll(createBox(0f, torsoY, 0f, 32f, 42f, 22f, tunicColor, Color(0x33000000)))

        // Celtic Belt & Golden Ring Buckle
        val beltY = 48f + breath
        polys.addAll(createBox(0f, beltY, 0f, 33.5f, 7f, 23.5f, Color(0xFF291A0E)))
        polys.addAll(createBox(0f, beltY, 12f, 9f, 9f, 2f, beltGold, Color(0xFF78350F)))

        // 5. Arms
        val leftArmX = -21f
        val rightArmX = 21f
        val armY = 32f + breath

        // Left arm (posed slightly forward)
        polys.addAll(createBox(leftArmX, armY, 2f, 9f, 32f, 11f, tunicColor))
        // Left hand
        polys.addAll(createBox(leftArmX, armY + 18f, 3f, 8f, 9f, 8f, skinColor))

        // Right arm
        polys.addAll(createBox(rightArmX, armY, 0f, 9f, 32f, 11f, tunicColor))
        // Right hand
        polys.addAll(createBox(rightArmX, armY + 18f, 0f, 8f, 9f, 8f, skinColor))

        // 6. Neck
        val neckY = 8f + breath
        polys.addAll(createBox(0f, neckY, 0f, 12f, 8f, 12f, skinColor))

        // 7. Head
        val headY = -8f + breath
        polys.addAll(createBox(0f, headY, 0f, 26f, 26f, 24f, skinColor, Color(0x22000000)))

        // Eyes (front face Z+)
        val eyeY = -7f + breath
        val eyeZ = 12.2f
        polys.addAll(createBox(-6f, eyeY, eyeZ, 4f, 4f, 1f, Color(0xFF1E293B)))
        polys.addAll(createBox(6f, eyeY, eyeZ, 4f, 4f, 1f, Color(0xFF1E293B)))
        // Eye shines
        polys.addAll(createBox(-5f, eyeY - 1f, eyeZ + 0.3f, 1.5f, 1.5f, 0.5f, Color.White))
        polys.addAll(createBox(7f, eyeY - 1f, eyeZ + 0.3f, 1.5f, 1.5f, 0.5f, Color.White))

        // Celtic smile
        polys.addAll(createBox(0f, -1f + breath, eyeZ, 6f, 2f, 0.5f, Color(0xFF991B1B)))

        // Woad War Paint (Blue Celtic markings on cheeks/forehead)
        if (customization.warPaintIndex > 0) {
            val woadBlue = Color(0xFF0284C7)
            when (customization.warPaintIndex) {
                1 -> {
                    // Swirls on cheeks
                    polys.addAll(createBox(-8f, -4f + breath, eyeZ + 0.2f, 3f, 5f, 0.5f, woadBlue))
                    polys.addAll(createBox(8f, -4f + breath, eyeZ + 0.2f, 3f, 5f, 0.5f, woadBlue))
                }
                2 -> {
                    // Triskelion mark on forehead
                    polys.addAll(createBox(0f, -15f + breath, eyeZ + 0.2f, 5f, 5f, 0.5f, Color(0xFF38BDF8)))
                }
                3 -> {
                    // Battle stripes
                    polys.addAll(createBox(-8f, -3f + breath, eyeZ + 0.2f, 5f, 2f, 0.5f, woadBlue))
                    polys.addAll(createBox(8f, -3f + breath, eyeZ + 0.2f, 5f, 2f, 0.5f, woadBlue))
                    polys.addAll(createBox(0f, -16f + breath, eyeZ + 0.2f, 8f, 2f, 0.5f, woadBlue))
                }
            }
        }

        // 8. Hair & Braids
        val hairTopY = -22f + breath
        polys.addAll(createBox(0f, hairTopY, 0f, 28f, 6f, 26f, hairColor)) // Top hair
        polys.addAll(createBox(-13f, -10f + breath, -1f, 4f, 22f, 24f, hairColor)) // Left hair
        polys.addAll(createBox(13f, -10f + breath, -1f, 4f, 22f, 24f, hairColor)) // Right hair
        polys.addAll(createBox(0f, -10f + breath, -12f, 28f, 22f, 4f, hairColor)) // Back hair

        // Celtic Braid in front
        polys.addAll(createBox(-9f, 2f + breath, 12.5f, 4f, 14f, 3f, hairColor))
        polys.addAll(createBox(9f, 2f + breath, 12.5f, 4f, 14f, 3f, hairColor))

        // --- 9. EQUIPPED ACCESSORIES ATTACHMENT ---
        val equippedMap = accessories.filter { it.isEquipped }.associateBy { it.slot }

        // HEAD ACCESSORY
        equippedMap[AccessorySlot.HEAD]?.let { headAcc ->
            when (headAcc.id) {
                "acc_crown_oak" -> {
                    // Crown of sacred oak leaves & golden acorns
                    val crownY = -20f + breath
                    val crownRadius = 16f
                    val crownSegments = 8
                    val leafColor = Color(0xFF166534)
                    val goldBerry = Color(0xFFF59E0B)

                    for (k in 0 until crownSegments) {
                        val ang = (k.toFloat() / crownSegments) * 2f * Math.PI.toFloat()
                        val lx = cos(ang) * crownRadius
                        val lz = sin(ang) * crownRadius
                        polys.addAll(createBox(lx, crownY, lz, 6f, 7f, 4f, leafColor, Color(0xFF0F4322)))
                        if (k % 2 == 0) {
                            polys.addAll(createBox(lx * 1.1f, crownY - 3f, lz * 1.1f, 3.5f, 3.5f, 3.5f, goldBerry))
                        }
                    }
                }
                "acc_helmet_horns" -> {
                    // Bronze helmet with curved horned antlers
                    val helmY = -23f + breath
                    val bronzeColor = Color(0xFFD97706)
                    val hornColor = Color(0xFFE5E7EB)
                    polys.addAll(createBox(0f, helmY, 0f, 29f, 10f, 27f, bronzeColor, Color(0xFF78350F)))
                    // Nose guard
                    polys.addAll(createBox(0f, -14f + breath, 14f, 4f, 10f, 2f, bronzeColor))
                    // Horns
                    polys.addAll(createBox(-17f, -28f + breath, 0f, 6f, 16f, 6f, hornColor))
                    polys.addAll(createBox(-21f, -38f + breath, 4f, 5f, 12f, 5f, hornColor))
                    polys.addAll(createBox(17f, -28f + breath, 0f, 6f, 16f, 6f, hornColor))
                    polys.addAll(createBox(21f, -38f + breath, 4f, 5f, 12f, 5f, hornColor))
                }
                "acc_gold_diadem" -> {
                    // Golden royal diadem
                    val diaY = -18f + breath
                    polys.addAll(createBox(0f, diaY, 13f, 24f, 5f, 2f, Color(0xFFFBBF24), Color(0xFFB45309)))
                    // Amber center gem
                    polys.addAll(createBox(0f, diaY - 3f, 14f, 6f, 6f, 3f, Color(0xFFEA580C)))
                }
                "acc_druid_hood" -> {
                    // Druid green cowl/hood
                    val hoodY = -16f + breath
                    polys.addAll(createBox(0f, hoodY, -5f, 32f, 28f, 28f, Color(0xFF14532D), Color(0xFF052E16)))
                }
                else -> {
                    // Default crown
                    polys.addAll(createBox(0f, -23f + breath, 0f, 28f, 5f, 26f, Color(headAcc.primaryColorHex)))
                }
            }
        }

        // NECK ACCESSORY (Torque / Amulet)
        equippedMap[AccessorySlot.NECK]?.let { neckAcc ->
            val torqueY = 9f + breath
            when (neckAcc.id) {
                "acc_torque_gold", "relic_torque" -> {
                    // Grand Gold Celtic Torc: thick golden ring with gap in front and bulbous beast knobs
                    val goldTorc = Color(0xFFF59E0B)
                    val torcKnob = Color(0xFFD97706)
                    // Ring segments around neck
                    polys.addAll(createBox(0f, torqueY, -9f, 18f, 4f, 3f, goldTorc)) // Back
                    polys.addAll(createBox(-10f, torqueY, 0f, 3f, 4f, 16f, goldTorc)) // Left
                    polys.addAll(createBox(10f, torqueY, 0f, 3f, 4f, 16f, goldTorc)) // Right
                    // Front knobs (open collar)
                    polys.addAll(createBox(-6f, torqueY, 9.5f, 5.5f, 5.5f, 5f, torcKnob, Color(0xFF78350F)))
                    polys.addAll(createBox(6f, torqueY, 9.5f, 5.5f, 5.5f, 5f, torcKnob, Color(0xFF78350F)))
                }
                "acc_triskel_amulet" -> {
                    // Hanging Triskelion Medallion
                    polys.addAll(createBox(0f, torqueY + 6f, 12f, 8f, 8f, 2f, Color(0xFF10B981), Color(0xFF047857)))
                    polys.addAll(createBox(0f, torqueY + 6f, 13f, 4f, 4f, 1f, Color(0xFFFBBF24)))
                }
                else -> {
                    polys.addAll(createBox(0f, torqueY, 0f, 20f, 4f, 20f, Color(neckAcc.primaryColorHex)))
                }
            }
        }

        // BODY ACCESSORY (Cloak / Tartan / Armor)
        equippedMap[AccessorySlot.BODY]?.let { bodyAcc ->
            when (bodyAcc.id) {
                "acc_tartan_cloak" -> {
                    // Flowing Red Tartan Cloak behind character
                    val cloakY = 38f + breath
                    val cloakZ = -13f
                    polys.addAll(createBox(0f, cloakY, cloakZ, 34f, 52f, 3f, Color(0xFFB91C1C), Color(0xFF7F1D1D)))
                    // Gold Celtic brooch on right shoulder
                    polys.addAll(createBox(12f, 14f + breath, 10f, 6f, 6f, 3f, Color(0xFFF59E0B), Color(0xFF92400E)))
                }
                "acc_bronze_armor" -> {
                    // Bronze Cuirass with spiral reliefs
                    val armorY = 32f + breath
                    polys.addAll(createBox(0f, armorY, 3f, 33f, 34f, 20f, Color(0xFFD97706), Color(0xFF78350F)))
                    // Center spiral medallion
                    polys.addAll(createBox(0f, armorY - 4f, 14f, 10f, 10f, 2f, Color(0xFFFBBF24)))
                }
                "acc_wolf_fur" -> {
                    // Wolf Fur Mantle on shoulders
                    val furY = 16f + breath
                    polys.addAll(createBox(0f, furY, 0f, 42f, 14f, 30f, Color(0xFF6B7280), Color(0xFF374151)))
                }
                "acc_daily_sun_cloak" -> {
                    // Radiant Golden Solar Cloak of the Solstice with sun brooch
                    val cloakY = 38f + breath
                    val cloakZ = -13f
                    polys.addAll(createBox(0f, cloakY, cloakZ, 36f, 54f, 4f, Color(0xFFF59E0B), Color(0xFFB45309)))
                    // Golden Sun disc clasp on chest
                    polys.addAll(createBox(0f, 16f + breath, 11f, 12f, 12f, 3f, Color(0xFFFDE047), Color(0xFFCA8A04)))
                    // Rays accent
                    polys.addAll(createBox(0f, 16f + breath, 12f, 16f, 4f, 2f, Color(0xFFFEF08A)))
                }
                else -> {
                    polys.addAll(createBox(0f, 36f + breath, -12f, 32f, 48f, 3f, Color(bodyAcc.primaryColorHex)))
                }
            }
        }

        // HAND ACCESSORY (Shield / Sword / Harp / Carnyx)
        equippedMap[AccessorySlot.HAND]?.let { handAcc ->
            when (handAcc.id) {
                "acc_celtic_shield" -> {
                    // Bronze Celtic Shield held in left hand
                    val shieldX = -25f
                    val shieldY = 46f + breath
                    val shieldZ = 12f
                    // Shield body
                    polys.addAll(createBox(shieldX, shieldY, shieldZ, 26f, 38f, 3f, Color(0xFF166534), Color(0xFFD97706)))
                    // Central bronze umbo (boss)
                    polys.addAll(createBox(shieldX, shieldY, shieldZ + 3f, 10f, 10f, 4f, Color(0xFFF59E0B), Color(0xFF78350F)))
                }
                "acc_bronze_sword", "relic_sword" -> {
                    // Celtic Bronze Sword with anthropomorphic hilt in right hand
                    val swordX = 26f
                    val swordY = 34f + breath
                    val swordZ = 14f
                    // Blade
                    polys.addAll(createBox(swordX, swordY - 14f, swordZ, 3.5f, 38f, 1.5f, Color(0xFFE2E8F0), Color(0xFF94A3B8)))
                    // Crossguard
                    polys.addAll(createBox(swordX, swordY + 6f, swordZ, 12f, 3f, 3f, Color(0xFFD97706)))
                    // Pommel
                    polys.addAll(createBox(swordX, swordY + 16f, swordZ, 5f, 5f, 3f, Color(0xFFF59E0B)))
                }
                "acc_bardo_harp" -> {
                    // Celtic Bard Harp
                    val harpX = 26f
                    val harpY = 40f + breath
                    val harpZ = 10f
                    // Triangular wooden frame
                    polys.addAll(createBox(harpX, harpY, harpZ, 4f, 32f, 4f, Color(0xFF78350F)))
                    polys.addAll(createBox(harpX - 6f, harpY - 14f, harpZ, 16f, 4f, 4f, Color(0xFF9A3412)))
                    polys.addAll(createBox(harpX - 10f, harpY + 8f, harpZ, 4f, 26f, 4f, Color(0xFFD97706)))
                    // Golden strings
                    polys.addAll(createBox(harpX - 4f, harpY, harpZ, 1f, 24f, 1f, Color(0xFFFBBF24)))
                }
                "acc_carnyx_horn", "relic_carnyx" -> {
                    // Vertical bronze Carnyx trumpet
                    val carnyxX = 26f
                    val carnyxY = 10f + breath
                    val carnyxZ = 10f
                    // Long tube
                    polys.addAll(createBox(carnyxX, carnyxY + 18f, carnyxZ, 4f, 54f, 4f, Color(0xFFD97706)))
                    // Boar head top
                    polys.addAll(createBox(carnyxX + 4f, carnyxY - 14f, carnyxZ, 14f, 10f, 8f, Color(0xFFF59E0B), Color(0xFF78350F)))
                }
                else -> {
                    polys.addAll(createBox(25f, 40f + breath, 10f, 6f, 28f, 6f, Color(handAcc.primaryColorHex)))
                }
            }
        }

        // PET ACCESSORY (Companion next to character)
        equippedMap[AccessorySlot.PET]?.let { petAcc ->
            when (petAcc.id) {
                "acc_morrigan_crow" -> {
                    // Raven of the Morrigan (perched on right shoulder or flying)
                    val wingFlap = sin(time * 8f) * 4f
                    val crowX = 22f
                    val crowY = -2f + breath
                    val crowZ = 6f
                    // Body & Head
                    polys.addAll(createBox(crowX, crowY, crowZ, 7f, 9f, 11f, Color(0xFF1E293B), Color(0xFF0F172A)))
                    // Beak
                    polys.addAll(createBox(crowX, crowY - 1f, crowZ + 8f, 2f, 3f, 5f, Color(0xFFF59E0B)))
                    // Wings
                    polys.addAll(createBox(crowX + 5f, crowY - 2f + wingFlap, crowZ, 10f, 2f, 8f, Color(0xFF0F172A)))
                    polys.addAll(createBox(crowX - 5f, crowY - 2f - wingFlap, crowZ, 10f, 2f, 8f, Color(0xFF0F172A)))
                }
                "acc_celtic_hound" -> {
                    // Loyal Celtic Hunting Hound standing beside left foot
                    val dogX = -38f
                    val dogY = 78f
                    val dogZ = 10f
                    // Dog Body
                    polys.addAll(createBox(dogX, dogY, dogZ, 14f, 16f, 26f, Color(0xFFD97706), Color(0xFF92400E)))
                    // Dog Head
                    polys.addAll(createBox(dogX, dogY - 10f, dogZ + 12f, 10f, 10f, 12f, Color(0xFFB45309)))
                    // Snout
                    polys.addAll(createBox(dogX, dogY - 8f, dogZ + 20f, 6f, 6f, 7f, Color(0xFF78350F)))
                    // Legs
                    polys.addAll(createBox(dogX - 5f, dogY + 14f, dogZ - 8f, 4f, 14f, 4f, Color(0xFFB45309)))
                    polys.addAll(createBox(dogX + 5f, dogY + 14f, dogZ - 8f, 4f, 14f, 4f, Color(0xFFB45309)))
                    polys.addAll(createBox(dogX - 5f, dogY + 14f, dogZ + 8f, 4f, 14f, 4f, Color(0xFFB45309)))
                    polys.addAll(createBox(dogX + 5f, dogY + 14f, dogZ + 8f, 4f, 14f, 4f, Color(0xFFB45309)))
                }
                "acc_white_stag" -> {
                    // Magical White Stag
                    val stagX = 40f
                    val stagY = 74f
                    val stagZ = 8f
                    polys.addAll(createBox(stagX, stagY, stagZ, 14f, 18f, 24f, Color(0xFFF8FAFC), Color(0xFFCBD5E1)))
                    polys.addAll(createBox(stagX, stagY - 14f, stagZ + 10f, 8f, 12f, 10f, Color(0xFFF8FAFC)))
                    // Golden Antlers
                    polys.addAll(createBox(stagX - 4f, stagY - 24f, stagZ + 10f, 3f, 12f, 3f, Color(0xFFF59E0B)))
                    polys.addAll(createBox(stagX + 4f, stagY - 24f, stagZ + 10f, 3f, 12f, 3f, Color(0xFFF59E0B)))
                }
                else -> {
                    polys.addAll(createBox(30f, 70f, 0f, 12f, 12f, 12f, Color(petAcc.primaryColorHex)))
                }
            }
        }

        return polys
    }
}
