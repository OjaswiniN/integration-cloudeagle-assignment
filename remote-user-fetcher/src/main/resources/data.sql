INSERT INTO EXTERNAL_ENDPOINT (id, name, url, http_method, headers_json, list_json_path)
VALUES (1, 'calendly', 'https://api.calendly.com/users/me', 'GET', '{"Authorization":"Bearer eyJraWQiOiIxY2UxZTEzNjE3ZGNmNzY2YjNjZWJjY2Y4ZGM1YmFmYThhNjVlNjg0MDIzZjdjMzJiZTgzNDliMjM4MDEzNWI0IiwidHlwIjoiUEFUIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJodHRwczovL2F1dGguY2FsZW5kbHkuY29tIiwiaWF0IjoxNzY0NDk5OTY1LCJqdGkiOiJkOGMxMTA3NC1lZmUyLTQ3ZDctOGE5OS1hNTVlNWYwYjAzZDUiLCJ1c2VyX3V1aWQiOiIxNmE2MmEyNy1mNGU2LTQ3ZmMtOWE4Ni1kODQzMzdmNDYwZjkifQ.aKQWVdw9l_8XGVreH371arLtTjyvubKsBK8srD6PD4rXtBrUju1MSR-CrPsQhsXkbkYTUlTwqOANUDcXJE9fig"}', '$.resource');

-- mapping: for Calendly response
INSERT INTO FIELD_MAPPING (id, endpoint_id, target_field, source_json_path)
VALUES (1, 1, 'userId', '$.slug'),
       (2, 1, 'fullName', '$.name'),
       (3, 1, 'email', '$.email');