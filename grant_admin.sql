-- Step 1: Find your user's ID
SELECT id, email, first_name, last_name FROM users WHERE email = 'your@email.com';
-- Copy the UUID from the id column

-- Step 2: Find the ADMIN role ID
SELECT id, name FROM roles WHERE name = 'ADMIN';
-- Copy the UUID from the id column

-- Step 3: Grant ADMIN role to your user (replace the UUIDs with actual values from steps 1 and 2)
INSERT INTO user_roles (user_id, role_id) 
VALUES ('paste-user-uuid-here', 'paste-admin-role-uuid-here');

-- Step 4: Verify the assignment
SELECT u.email, r.name 
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.email = 'your@email.com';
