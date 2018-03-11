import sqlite3

conn = sqlite3.connect('app.db')

ls = ['data1.sql', 'data2.sql']
for s in ls:
    j = open(s)
    for i in j:
        conn.execute(i)

    conn.commit()
    j.close()

conn.close()