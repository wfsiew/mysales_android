import sqlite3, os

def createdb():
    conn = None

    try:
        f = 'app.db'
        if os.path.exists(f):
            os.remove(f)

        conn = sqlite3.connect(f)
        with open('create.sql') as k:
            for i in k:
                conn.execute(i)

        conn.commit()

        ls = ['data1.sql', 'data2.sql']
        for s in ls:
            with open(s) as j:
                for i in j:
                    conn.execute(i)

            conn.commit()

    finally:
        if conn is not None:
            conn.close()

if __name__ == '__main__':
    createdb()