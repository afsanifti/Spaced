package com.example.spaced.ui.components.session

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.dp

private const val MESH_GRADIENT_SHADER = """
    uniform float u_time;
    uniform vec2 u_resolution;
    
    uniform vec2 u_p1, u_p2, u_p3, u_p4;
    uniform vec3 u_c1, u_c2, u_c3, u_c4;
    uniform vec3 u_surface;
    
    uniform float u_blobScale;
    uniform float u_edgeSoftness;
    uniform float u_opacity;

    vec2 calculateOffset(vec2 base, float time, float speedX, float speedY, float scale) {
        float dx = sin(time * speedX) * 0.08 * scale + cos(time * speedY * 0.5) * 0.03 * scale;
        float dy = cos(time * speedY) * 0.08 * scale + sin(time * speedX * 0.5) * 0.03 * scale;
        return clamp(base + vec2(dx, dy), vec2(0.0), vec2(1.0));
    }

    float getBlobWeight(vec2 uv, vec2 center, float radius, float softness) {
        float d = distance(uv, center);
        float sigma = radius * mix(0.4, 0.85, softness);
        return exp(-0.5 * pow(d / sigma, 2.0));
    }

    vec4 main(vec2 fragCoord) {
        vec2 uv = fragCoord / u_resolution;

        vec2 p1 = calculateOffset(u_p1, u_time, 0.35, 0.25, u_blobScale);
        vec2 p2 = calculateOffset(u_p2, u_time, -0.28, 0.32, u_blobScale);
        vec2 p3 = calculateOffset(u_p3, u_time, 0.22, -0.38, u_blobScale);
        vec2 p4 = calculateOffset(u_p4, u_time, -0.30, -0.20, u_blobScale);

        float radius = u_blobScale * 0.65;
        float w1 = getBlobWeight(uv, p1, radius, u_edgeSoftness);
        float w2 = getBlobWeight(uv, p2, radius, u_edgeSoftness);
        float w3 = getBlobWeight(uv, p3, radius, u_edgeSoftness);
        float w4 = getBlobWeight(uv, p4, radius, u_edgeSoftness);

        vec3 accumulatedMesh = u_c1 * w1 + u_c2 * w2 + u_c3 * w3 + u_c4 * w4;
        float totalWeight = max(w1 + w2 + w3 + w4, 0.001);
        vec3 normalizedMesh = accumulatedMesh / totalWeight;

        float maxAlpha = clamp((w1 + w2 + w3 + w4) * 0.5, 0.0, 1.0) * u_opacity;

        vec3 finalColor = mix(u_surface, normalizedMesh, maxAlpha);

        return vec4(finalColor, 1.0);
    }
"""

@Composable
fun AmbientMeshCard(
    modifier: Modifier = Modifier,
    animationDurationMillis: Int = 8000,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val color1 = MaterialTheme.colorScheme.secondary
    val color2 = MaterialTheme.colorScheme.secondaryContainer
    val color3 = MaterialTheme.colorScheme.secondaryContainer
    val color4 = MaterialTheme.colorScheme.secondary

    val infiniteTransition = rememberInfiniteTransition(label = "MeshAmbientAnimation")
    val shaderTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 62.83f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Reverses direction instead of jumping back to start
        ),
        label = "ShaderTime"
    )

    val backgroundModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Modifier.meshGradientShader(
            shaderTime = shaderTime,
            surfaceColor = surfaceColor,
            color1 = color1,
            color2 = color2,
            color3 = color3,
            color4 = color4
        )
    } else {
        Modifier.background(
            Brush.radialGradient(
                colors = listOf(color4.copy(alpha = 0.4f), color1.copy(alpha = 0.2f), surfaceColor),
                radius = 800f
            )
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(36.dp)),
        color = surfaceColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(backgroundModifier),
            content = content
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun Modifier.meshGradientShader(
    shaderTime: Float,
    surfaceColor: Color,
    color1: Color,
    color2: Color,
    color3: Color,
    color4: Color
): Modifier {
    val runtimeShader = remember { RuntimeShader(MESH_GRADIENT_SHADER) }
    return this.drawWithCache {
        val brush = ShaderBrush(runtimeShader)
        onDrawBehind {
            runtimeShader.setFloatUniform("u_time", shaderTime)
            runtimeShader.setFloatUniform("u_resolution", size.width, size.height)

            runtimeShader.setFloatUniform("u_p1", 0.18f, 0.22f)
            runtimeShader.setFloatUniform("u_p2", 0.82f, 0.24f)
            runtimeShader.setFloatUniform("u_p3", 0.26f, 0.80f)
            runtimeShader.setFloatUniform("u_p4", 0.78f, 0.76f)

            runtimeShader.setFloatUniform("u_c1", color1.red, color1.green, color1.blue)
            runtimeShader.setFloatUniform("u_c2", color2.red, color2.green, color2.blue)
            runtimeShader.setFloatUniform("u_c3", color3.red, color3.green, color3.blue)
            runtimeShader.setFloatUniform("u_c4", color4.red, color4.green, color4.blue)

            runtimeShader.setFloatUniform("u_surface", surfaceColor.red, surfaceColor.green, surfaceColor.blue)

            runtimeShader.setFloatUniform("u_blobScale", 0.72f)
            runtimeShader.setFloatUniform("u_edgeSoftness", 0.70f)
            runtimeShader.setFloatUniform("u_opacity", 0.30f)

            drawRect(brush)
        }
    }
}