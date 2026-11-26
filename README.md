# AI Film Recommender

AI Film Recommender adalah aplikasi Spring Boot sederhana untuk mencari film dari katalog pilihan. Pengguna memilih genre dan menjelaskan film yang sedang dicari. Spring AI meneruskan permintaan beserta kandidat film yang sesuai ke Ollama lokal. Model hanya boleh memilih dari kandidat tersebut, lalu aplikasi memeriksa jawabannya sebelum menyimpan rekomendasi ke H2.

## Yang dibutuhkan

- JDK 17 atau lebih baru
- Ollama yang berjalan di komputer lokal
- Model chat yang sudah tersedia di Ollama, misalnya `llama3.2`
- Maven tidak perlu dipasang terpisah; proyek sudah menyertakan Maven Wrapper

## Menjalankan aplikasi

Pastikan Ollama berjalan dan model sudah diunduh. Jika belum:

```powershell
ollama pull llama3.2
```

Di terminal PowerShell pada folder proyek, pilih model lalu jalankan aplikasi:

```powershell
$env:SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL = "llama3.2"
.\mvnw.cmd spring-boot:run
```

Buka <http://localhost:8080> di browser. Jika port 8080 sedang dipakai aplikasi lain, jalankan di port 18080:

```powershell
.\mvnw.cmd '-Dspring-boot.run.arguments=--server.port=18080' spring-boot:run
```

Lalu buka <http://localhost:18080>.

Aplikasi memakai endpoint lokal bawaan Ollama dan tidak memerlukan API key cloud. Halaman tetap bisa dibuka tanpa Ollama, tetapi permintaan rekomendasi membutuhkan layanan Ollama dan model yang dipilih sedang aktif.

## Penyimpanan data

Katalog film contoh diisi otomatis saat aplikasi mulai. Riwayat rekomendasi disimpan di `data/film-recommender.mv.db`; folder `data/` tidak ikut dilacak Git. Untuk demo ini, H2 memakai akun lokal `sa` tanpa password. Konfigurasi tersebut hanya ditujukan untuk penggunaan lokal, bukan produksi.

## Menjalankan tes

Tes otomatis memakai H2 dalam memori dan klien pemilih film tiruan. Tes tidak menghubungi Ollama dan tidak mengunduh model.

```powershell
.\mvnw.cmd test
```

## Dokumentasi

- [Latar belakang, cara kerja, dan catatan eksperimen](docs/experiment-report.md)
- [Diagram UML dan model data](docs/data-model.md)
- [Plan implementasi](docs/superpowers/plans/2026-11-27-ai-film-recommender.md)

## Batasan aplikasi

Katalog berisi sejumlah kecil film pilihan. Aplikasi belum memiliki akun pengguna, fitur pengelolaan katalog, API katalog eksternal, atau RAG. Model membantu memilih dan menjelaskan film; model tidak boleh menambahkan judul di luar kandidat yang diberikan. Sebelum menyimpan hasil, aplikasi menolak ID yang tidak dikenal, pilihan duplikat, lebih dari tiga hasil, dan alasan yang tidak valid.

Proyek menargetkan Java 17 dan menggunakan Spring Boot 4.1.0 serta Spring AI 2.0.0. Kecocokan versi merujuk pada [pengumuman Spring AI 2.0 GA](https://spring.io/blog/2026/06/12/spring-ai-2-0-0-GA-available-now/) dan [panduan Spring AI untuk Ollama](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html).
