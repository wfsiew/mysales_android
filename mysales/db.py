import sqlite3

def createdb():
    conn = None

    try:
        conn = sqlite3.connect('app.db')
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