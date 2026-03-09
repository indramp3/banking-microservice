# Konfigurasi Database

Dokumen ini menjelaskan konfigurasi database yang digunakan dalam aplikasi Demo Banking.

## Ringkasan Konfigurasi
Aplikasi ini menggunakan MySQL sebagai database relasional utama. Kedua layanan mikro (`account-service` dan `transaction-service`) berbagi database yang sama namun mengelola tabel yang berbeda.

### Detail Koneksi
- **Driver**: `com.mysql.cj.jdbc.Driver`
- **Host**: `127.0.0.1`
- **Port**: `3306`
- **Database Name**: `demo_bank`
- **Username**: `root`
- **Password**: `root`

## Skema Database
Skema database didefinisikan secara otomatis oleh Hibernate (`ddl-auto: update`), namun Anda dapat menemukan skema awal di file [initial_schema.sql](./initial_schema.sql).

### Tabel Utama:
1.  **ACCOUNT_MASTER**: Menyimpan data nasabah dan saldo akun.
2.  **transactions**: Menyimpan riwayat transaksi (transfer/topup).
3.  **TRANSACTION_HISTORY**: Menyimpan log status perubahan dari setiap transaksi.

## Cara Menjalankan Secara Lokal
Jika Anda menjalankan database menggunakan Docker atau MySQL lokal, pastikan database `demo_bank` sudah dibuat atau biarkan aplikasi membuatnya saat pertama kali dijalankan dengan parameter `createDatabaseIfNotExist=true` pada URL JDBC.

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/demo_bank?createDatabaseIfNotExist=true
    username: root
    password: root
```
