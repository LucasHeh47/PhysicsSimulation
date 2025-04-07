package com.lucasj.PhysicsSimulation.Utils;

import java.awt.Color;

// Color gradient for mass visualization
/*
 * made with ChatGPT with the following prompt
 * "make a java method that takes a number (1 - 1000) 
 * and returns a color from (starting at 1) (#8adcff or rgb(138 220 255)) 
 * to 1000 (#ff2a2a or rgb(255 42 42)) using OKLAB mode"
 */
public class ColorInterpolation {

    // Convert sRGB component to linear RGB
    private static double srgbToLinear(double c) {
        return (c <= 0.04045) ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    // Convert linear RGB component to sRGB
    private static double linearToSrgb(double c) {
        return (c <= 0.0031308) ? 12.92 * c : 1.055 * Math.pow(c, 1.0 / 2.4) - 0.055;
    }

    // A simple container for OKLAB components
    private static class Oklab {
        double L, a, b;
        Oklab(double L, double a, double b) {
            this.L = L;
            this.a = a;
            this.b = b;
        }
    }

    // Convert from a Color (sRGB) to OKLAB
    private static Oklab rgbToOklab(Color color) {
        // Normalize sRGB values to [0,1]
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        // Convert to linear sRGB
        double rLin = srgbToLinear(r);
        double gLin = srgbToLinear(g);
        double bLin = srgbToLinear(b);

        // Convert linear sRGB to LMS using the forward matrix
        double L = 0.4122214708 * rLin + 0.5363325363 * gLin + 0.0514459929 * bLin;
        double M = 0.2119034982 * rLin + 0.6806995451 * gLin + 0.1073969566 * bLin;
        double S = 0.0883024619 * rLin + 0.2817188376 * gLin + 0.6299787005 * bLin;

        // Take cube roots of LMS values
        double l_ = Math.cbrt(L);
        double m_ = Math.cbrt(M);
        double s_ = Math.cbrt(S);

        // Convert to OKLAB with the second matrix
        double L_oklab = 0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_;
        double a_oklab = 1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_;
        double b_oklab = 0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_;

        return new Oklab(L_oklab, a_oklab, b_oklab);
    }

    // Convert from OKLAB back to sRGB Color
    private static Color oklabToRgb(Oklab lab) {
        double L = lab.L;
        double a = lab.a;
        double b = lab.b;

        // Inverse of the second matrix to get cube roots of LMS
        double l_ = L + 0.3963377774 * a + 0.2158037573 * b;
        double m_ = L - 0.1055613458 * a - 0.0638541728 * b;
        double s_ = L - 0.0894841775 * a - 1.2914855480 * b;

        // Cube to get LMS values back
        double L_cubed = l_ * l_ * l_;
        double M_cubed = m_ * m_ * m_;
        double S_cubed = s_ * s_ * s_;

        // Convert LMS back to linear sRGB using the inverse matrix
        double rLin =  4.0767416621 * L_cubed - 3.3077115913 * M_cubed + 0.2309699292 * S_cubed;
        double gLin = -1.2684380046 * L_cubed + 2.6097574011 * M_cubed - 0.3413193965 * S_cubed;
        double bLin = -0.0041960863 * L_cubed - 0.7034186147 * M_cubed + 1.7076147010 * S_cubed;

        // Convert linear sRGB to sRGB and scale to [0, 255]
        int r = (int) Math.round(255 * linearToSrgb(rLin));
        int g = (int) Math.round(255 * linearToSrgb(gLin));
        int b2 = (int) Math.round(255 * linearToSrgb(bLin));

        // Clamp values to ensure they lie within [0, 255]
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b2 = Math.min(255, Math.max(0, b2));

        return new Color(r, g, b2);
    }

    /**
     * Returns a Color by interpolating in OKLAB space between:
     * - Starting color: #8adcff (rgb(138,220,255)) at value 1
     * - Ending color:   #ff2a2a (rgb(255,42,42)) at value 1000
     *
     * @param value An integer between 1 and 1000.
     * @return Interpolated Color.
     */
    public static Color getInterpolatedColor(int value) {
        // Ensure value is within the expected range
        int clampedValue = Math.min(1000, Math.max(1, value));
        double t = (clampedValue - 1) / 999.0;  // normalized interpolation factor [0,1]

        // Define the starting and ending colors
        Color start = new Color(0x8a, 0xdc, 0xff); // #8adcff
        Color end = new Color(0xff, 0x2a, 0x2a);   // #ff2a2a

        // Convert the colors to OKLAB
        Oklab startOklab = rgbToOklab(start);
        Oklab endOklab = rgbToOklab(end);

        // Interpolate each OKLAB component
        double L = startOklab.L + t * (endOklab.L - startOklab.L);
        double a = startOklab.a + t * (endOklab.a - startOklab.a);
        double b = startOklab.b + t * (endOklab.b - startOklab.b);

        // Convert the interpolated OKLAB value back to sRGB
        return oklabToRgb(new Oklab(L, a, b));
    }
}