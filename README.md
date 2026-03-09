# DEMO BANKING

Sebuah aplikasi perbankan berbasis arsitektur microservices yang dirancang untuk menangani manajemen akun dan transaksi secara efisien.

## Ringkasan Proyek
Proyek ini terdiri dari beberapa layanan mandiri yang berkomunikasi satu sama lain untuk menyediakan fitur perbankan yang lengkap:

- **Layanan Gateway (Gateway Service)**: Bertindak sebagai API Gateway untuk merutekan permintaan ke layanan yang sesuai.
- **Layanan Akun (Account Service)**: Mengelola data nasabah dan informasi akun perbankan.
- **Layanan Transaksi (Transaction Service)**: Menangani proses transaksi perbankan (transfer, deposit, dsb).
- **Layanan Pemantauan (Monitoring Service)**: Digunakan untuk memantau metrik dan kesehatan aplikasi.

## Alur Aplikasi (Application Flow)
Aplikasi ini menggunakan pola **Request-Reply** melalui Kafka untuk komunikasi antar-layanan melalui Gateway:

1. **Client -> Gateway (HTTP)**: Pengguna mengirimkan permintaan REST API ke Gateway.
2. **Gateway -> Kafka (Request)**: Gateway memproduksi pesan ke topik Kafka yang sesuai (misal: `create-account-req`).
3. **Service -> Kafka (Reply)**: Layanan tujuan (Account/Transaction) memproses permintaan dan mengirimkan balasan ke topik balasan Kafka.
4. **Gateway -> Client (HTTP)**: Gateway menerima balasan dari Kafka dan meneruskannya kembali ke pengguna sebagai respons HTTP.

*Khusus untuk Transaksi: Layanan Transaksi juga berkomunikasi dengan Layanan Akun via Kafka untuk memverifikasi dan memperbarui saldo secara asinkron.*

## Daftar Endpoint API

### 1. Manajemen Akun (`/account`)
- `POST /account/create`: Membuat akun perbankan baru.
- `GET /account/search`: Mencari data akun berdasarkan NIK atau Nomor Akun.
- `GET /account/balance`: Memeriksa saldo terkini dari suatu akun.

### 2. Transaksi (`/transaction`)
- `POST /transaction/create`: Membuat transaksi transfer antar-akun.
- `GET /transaction/account`: Mendapatkan riwayat transaksi untuk nomor akun tertentu.
- `GET /transaction/detail`: Mendapatkan rincian lengkap dari sebuah ID transaksi.
- `GET /transaction/history`: Mencari riwayat transaksi berdasarkan rentang tanggal.
- `POST /transaction/topup`: Menambahkan saldo (top up) ke akun tertentu.

### 3. Pemantauan (`/monitoring`)
- `GET /monitoring/metrics`: Mengambil metrik bisnis gabungan dari seluruh layanan.
- `GET /monitoring/health`: Memeriksa status kesehatan (health check) sistem secara keseluruhan.

## Teknologi yang Digunakan

### Framework & Bahasa Pemrograman Utama
- **Java 21 & 17**: Bahasa pemrograman utama yang digunakan di berbagai layanan.
- **Spring Boot 3.5.11**: Framework utama untuk membangun layanan mikro.
- **Maven**: Alat manajemen proyek dan build.

### Database & Persistensi Data
- **MySQL**: Database relasional untuk menyimpan data akun dan transaksi.
- **Spring Data JPA**: Abstraksi lapisan akses data menggunakan Hibernate.
- **Dokumentasi Database**: Detail konfigurasi dan skema dapat dilihat di folder [database](./database/README_DB.md).

### Infrastruktur & Pesan (Messaging)
- **Apache Kafka**: Sistem pengiriman pesan terdistribusi untuk komunikasi antar-layanan secara asinkron.
- **Zookeeper**: Manajemen koordinasi untuk klaster Kafka.
- **Docker Compose**: Orkestrasi untuk menjalankan infrastruktur (Kafka, Zookeeper) di lingkungan lokal.

### Pustaka (Library) Pendukung
- **Lombok**: Untuk mengurangi kode boilerplate di Java.
- **Spring Kafka**: Integrasi Spring dengan Apache Kafka.