import sqlite3

conn = sqlite3.connect('app.db')

j = open('data.sql')
for s in j:
    conn.execute(s)

conn.commit()
conn.close()