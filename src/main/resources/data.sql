INSERT IGNORE INTO users
    (username, email, password, role, active, created_at)
VALUES
(
    'admin',
    'admin@zorvyn.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMIN',
    true,
    NOW()
),
(
    'analyst',
    'analyst@zorvyn.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ANALYST',
    true,
    NOW()
),
(
    'viewer',
    'viewer@zorvyn.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'VIEWER',
    true,
    NOW()
);