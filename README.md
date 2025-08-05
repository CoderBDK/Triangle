# Triangle Rotation & Angle Detection

A Jetpack Compose based Android component to draw a triangle with draggable points, enabling smooth 360° rotation and real-time calculation of interior angles.

---

## Features

- **Draggable Points:** Select and drag individual triangle points.
- **Smooth Rotation:** Rotate the triangle smoothly with drag gestures.
- **Angle Calculation:** Automatically computes and displays the interior angles of the triangle.
- **Visual Feedback:** Highlights selected points and connected edges.
- **Interactive UI:** Displays direction angles and rotation angle dynamically.

---

## Preview

![Triangle Rotation Preview](https://github.com/user-attachments/assets/7d98165c-8019-4bb7-b57c-f3eea12862fe)

---

## Usage

Add the `Triangle` Cmposable to your Compose UI hierarchy:

```kotlin
Triangle(modifier = Modifier.fillMaxSize())
