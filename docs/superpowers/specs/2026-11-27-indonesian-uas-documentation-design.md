# Rancangan Dokumentasi UAS Berbahasa Indonesia

## Tujuan

Membuat dokumentasi yang dibaca pengguna proyek mudah dipahami dalam Bahasa Indonesia yang wajar, konsisten dengan aplikasi, dan tidak mengklaim hasil eksperimen yang belum diamati.

## Pilihan pendekatan

- Menerjemahkan semua berkas, termasuk plan eksekusi internal. Ini membuat seluruh folder docs seragam, tetapi mengubah catatan kerja yang memang disusun untuk alat eksekusi.
- Menerjemahkan README, laporan eksperimen, dan model data; membiarkan plan internal tetap dalam Bahasa Inggris. Ini dipilih pengguna karena mempertahankan format teknis plan sekaligus membuat dokumentasi proyek mudah dibaca.

## Cakupan dan batasan

Yang diubah: `README.md`, `docs/experiment-report.md`, dan `docs/data-model.md`. Nama class, endpoint, properti konfigurasi, perintah, serta diagram kode tetap memakai istilah teknis aslinya agar dapat dicari dan dijalankan.

Plan di `docs/superpowers/plans/` tidak diterjemahkan. Tidak ada perubahan pada aplikasi atau skema database dalam pekerjaan dokumentasi ini.

Dokumentasi hanya menyebut pengujian yang memang terverifikasi. Keberhasilan unduh Ollama dan model `llama3.2` dapat dicatat dari hasil yang tersedia; isi rekomendasi langsung, penilaian kualitas, dan screenshot tidak akan direka. Bagian hasil eksperimen langsung akan ditandai untuk dilengkapi setelah keluaran nyata tersedia.

## Kriteria selesai

- README, laporan eksperimen, dan model data berbahasa Indonesia yang lugas dan tidak berulang.
- Instruksi tetap dapat dijalankan dan istilah teknis konsisten dengan kode.
- Fakta tentang tes, Ollama, model, port, dan keluaran percobaan sesuai bukti yang tersedia.
- Tidak ada hasil model atau screenshot yang diklaim tanpa bukti.
