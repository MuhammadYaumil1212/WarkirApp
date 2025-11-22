# Warkir App - Project Specification

Warkir App adalah aplikasi Android Native berbasis XML yang dikembangkan untuk memberikan pengalaman pemesanan UMKM kampus yang praktis, cepat, dan modern.

---

## Libraries Used

Berikut library utama yang digunakan pada project ini:

| Library | Fungsi | Keterangan |
|--------|--------|------------|
| AndroidX Core KTX | Utilitas Kotlin untuk Android | `implementation(libs.androidx.core.ktx)` |
| AppCompat | Support modern UI pada berbagai versi Android | `implementation(libs.androidx.appcompat)` |
| Material Components | Material Design 3 UI elements | `implementation(libs.material)` |
| Activity KTX | Komponen Activity modern | `implementation(libs.androidx.activity)` |
| ConstraintLayout | Layout fleksibel untuk tampilan yang kompleks | `implementation(libs.androidx.constraintlayout)` |
| JUnit | Unit Testing | `testImplementation(libs.junit)` |
| Espresso | UI Testing | `androidTestImplementation(libs.androidx.espresso.core)` |
| AndroidX JUnit | Instrumentation Test | `androidTestImplementation(libs.androidx.junit)` |

> Catatan: Semua library dikelola melalui Gradle Version Catalog (`libs.versions.toml`)

---

## Project Specifications

| Spesifikasi | Detail |
|-----------|--------|
| Bahasa | Kotlin |
| UI Layout | XML Native |
| Build Tools | Android Gradle Plugin dengan Version Catalog |
| View Binding | Aktif |
| Minify | Off (Release) |
| JDK | Java 11 |
| Target SDK | 36 (Android 15) |
| Compile SDK | 36 |
| Architecture | Native XML dengan prinsip MVVM (optional, menyesuaikan kebutuhan) |
| Theme | Material 3 + Edge-to-Edge UI Support |

---

## Supported Android Versions

| Parameter | Nilai |
|----------|------|
| Min SDK | 24 → Android 7.0 (Nougat) |
| Target SDK | 36 → Android 15 |
| Device Support | Phone & Tablet (Portrait recommended) |

---

## Fitur Utama (Planned)

- Desain UI Modern
- Navigasi sederhana & responsif
- Kompatibel Edge-to-Edge Layout
- Performa optimal dengan ViewBinding

---





# Warkir App Color Palette

Berikut adalah panduan penggunaan warna utama untuk aplikasi Android Native (XML).

## Primary Colors

**Primary — `#369EB2`**
- Warna utama brand aplikasi
- Digunakan pada tombol utama dan elemen highlight

**Primary Dark — `#287D8C`**
- Untuk AppBar/Toolbar, Status Bar, Bottom Navigation aktif
- Cocok untuk state ditekan (pressed)

**Primary Light — `#A6D9E1`**
- Latar belakang elemen ringan seperti Card, Chip, dan TextField

---

## Secondary & Accent Colors

**Secondary — `#FFB703`**
- Call To Action (CTA): Pesan, Bayar, Submit
- Badge penting yang perlu perhatian khusus

**Accent/Tertiary — `#2A4858`**
- Teks judul dan ikon aktif
- Divider kuat atau highlight tegas

---

## Neutral Colors (Typography & Background)

**Background — `#EEF2F5`**
- Warna dasar layout agar terlihat bersih dan modern

**Text Primary — `#1B1F23`**
- Utama untuk judul dan paragraf penting
- Kontras tinggi dan mudah dibaca

**Text Secondary — `#6A767E`**
- Untuk deskripsi dan informasi tambahan
- Memberikan hirarki visual yang nyaman

---

## Catatan
- Palet ini mendukung tampilan modern *edge-to-edge*
- Mudah digunakan untuk Light Mode (Dark Mode dapat ditambahkan)
- Konsisten digunakan untuk keseluruhan aplikasi

