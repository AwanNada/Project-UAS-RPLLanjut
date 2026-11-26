# Laporan Eksperimen

## Gambaran 5W1H

### Siapa

Pengguna aplikasi adalah orang yang sedang mencari film untuk ditonton. Aplikasi ini dibuat sebagai proyek UAS Rekayasa Perangkat Lunak Lanjut, bukan sebagai layanan rekomendasi produksi. Aplikasi tidak menyimpan akun atau profil pribadi.

### Apa

Pengguna memilih genre dan menuliskan preferensi dengan panjang maksimal 300 karakter. Spring AI meminta model Ollama lokal memilih paling banyak tiga film dari katalog yang cocok, lalu memberi alasan singkat. Aplikasi menampilkan hasil dan menyimpan permintaan yang berhasil ke riwayat.

### Di mana

Aplikasi berjalan secara lokal melalui Spring Boot. Alamat bawaan adalah `http://localhost:8080`; jika port tersebut sedang dipakai, aplikasi dapat dijalankan di port lain, misalnya `18080`. Ollama menggunakan endpoint lokal bawaannya. Data aplikasi tersimpan di `data/film-recommender.mv.db`, sedangkan tes memakai H2 dalam memori.

### Kapan

Eksperimen dilakukan pada 27 November 2026 di Windows. Riwayat dibuat hanya jika aplikasi menerima pilihan yang valid. Katalog kosong, jawaban model yang tidak valid, dan kegagalan penyedia tidak menghasilkan entri riwayat.

### Mengapa

Eksperimen ini menguji penggunaan model bahasa untuk membantu memilih film, dengan ruang pilihan yang tetap dibatasi katalog. Model memberi pilihan dan alasan; katalog tetap menjadi sumber judul, genre, tahun, dan sinopsis yang boleh direkomendasikan.

### Bagaimana

1. Pengguna mengirim genre dan preferensi dari formulir browser.
2. Aplikasi menolak genre kosong, preferensi kosong, atau preferensi yang lebih dari 300 karakter sebelum memanggil model.
3. Aplikasi mencari film yang genrenya sesuai. Jika tidak ada kandidat, aplikasi menampilkan pesan bahwa film tidak ditemukan dan tidak menyimpan permintaan.
4. Spring AI mengirim preferensi serta kandidat yang ditemukan ke Ollama, lalu meminta jawaban terstruktur berisi ID film dan alasan.
5. Aplikasi memeriksa bahwa jawaban terbaca, setiap ID berasal dari kandidat, tidak ada ID duplikat, jumlah hasil paling banyak tiga, dan setiap alasan valid.
6. Pilihan yang lolos pemeriksaan disimpan sebagai permintaan dan item berperingkat, lalu ditampilkan bersama riwayat.

## Catatan pelaksanaan

**Tanggal pemeriksaan:** 27 November 2026

**Lingkungan:** Windows. Tes dijalankan dengan Java 26.0.2; target kompilasi proyek adalah Java 17. Runtime Java 17 tidak tersedia untuk pemeriksaan terpisah. Versi lain yang digunakan: Spring Boot 4.1.0, Spring AI 2.0.0, dan H2 2.4.240. Perintah build dijalankan melalui Maven Wrapper.

| Pemeriksaan | Hasil |
| --- | --- |
| Ollama sebelum instalasi | Pada pemeriksaan awal, perintah `ollama` belum tersedia. Ollama tidak dipasang oleh proses pengembangan ini. |
| Ollama setelah dipasang pengguna | `ollama --version` menampilkan 0.40.18. `ollama list` menampilkan `llama3.2:latest`, berukuran 2,0 GB. Pengguna juga melaporkan proses `ollama pull llama3.2` selesai dengan status `success`. |
| Tes otomatis | Lulus 20/20: tes konteks aplikasi, katalog, layanan rekomendasi, adapter Spring AI, alur MVC, dan persistensi. Klien model pada tes memakai stub; hasil ini bukan hasil dari Ollama. |
| Persistensi H2 file | Lulus. Tes membuat permintaan beserta itemnya, menutup konteks aplikasi, membuka konteks baru pada file H2 yang sama, lalu memuat kembali data tersebut. |
| Pemeriksaan halaman melalui HTTP | Lulus. Halaman utama mengembalikan HTTP 200 di port 18080 dan memuat judul aplikasi, pilihan genre, serta batas 300 karakter. Port 8080 sedang digunakan di lingkungan pemeriksaan tersebut. |
| Uji rekomendasi langsung | Pengguna melaporkan aplikasi berhasil digunakan setelah memasang model. Namun, judul dan alasan dari hasil model tidak tersimpan sebagai catatan yang dapat diperiksa ulang. Karena itu laporan ini tidak mengutip hasil atau menilai kualitas rekomendasinya. |
| Screenshot hasil rekomendasi | Belum tersedia. Screenshot yang tercatat hanya menampilkan formulir, bukan kartu hasil model. |

### Bukti uji otomatis

Jalankan seluruh tes dari terminal PowerShell pada folder proyek:

```powershell
.\mvnw.cmd test
```

Tes yang memeriksa batas dan kegagalan menggunakan pemilih film tiruan agar dapat dijalankan tanpa koneksi ke Ollama:

| Kasus | Hasil yang diperiksa |
| --- | --- |
| Preferensi lebih dari 300 karakter | Formulir menampilkan kesalahan; pemilih model tidak dipanggil dan permintaan tidak disimpan. |
| Genre tidak memiliki film di katalog | Aplikasi menyatakan tidak ada hasil; pemilih model tidak dipanggil dan permintaan tidak disimpan. |
| Model mengembalikan ID film yang tidak dikenal | Pilihan ditolak dan permintaan tidak disimpan. |
| Model mengembalikan ID yang sama lebih dari sekali | Pilihan ditolak dan permintaan tidak disimpan. |
| Model mengembalikan lebih dari tiga film | Pilihan ditolak dan permintaan tidak disimpan. |
| Database file dibuka ulang | Permintaan dan item berperingkat dapat dimuat kembali setelah konteks aplikasi dibuka ulang. |

Tes otomatis tersebut memverifikasi perilaku aplikasi. Tes itu tidak mengukur mutu jawaban, waktu respons, atau konsistensi model bahasa.

### Mencoba model secara lokal

Jika Ollama belum memiliki model, unduh terlebih dahulu:

```powershell
ollama pull llama3.2
```

Kemudian, di terminal PowerShell pada folder proyek:

```powershell
$env:SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL = "llama3.2"
.\mvnw.cmd spring-boot:run
```

Buka `http://localhost:8080`. Jika port 8080 sudah dipakai program lain, jalankan di port 18080:

```powershell
.\mvnw.cmd '-Dspring-boot.run.arguments=--server.port=18080' spring-boot:run
```

Lalu buka `http://localhost:18080`. Setelah mencoba, lengkapi catatan eksperimen dengan tanggal, genre, preferensi yang dikirim, nama/tag model, judul dan alasan yang benar-benar tampil, serta screenshot halaman hasil. Jangan gunakan respons stub dari tes sebagai hasil eksperimen model.

## Batasan

- Katalog berisi 14 film dalam tujuh genre; pilihan model terbatas pada katalog tersebut.
- Belum ada pengukuran mutu rekomendasi, waktu respons, atau perbandingan antar-model.
- H2 dan Ollama lokal digunakan untuk demo; autentikasi pengguna, migrasi skema produksi, dan pengamanan deployment tidak termasuk cakupan.
