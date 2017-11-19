from openpyxl import load_workbook

def readfile():
    o = open('data.sql', 'w')
    wb = load_workbook('sample.xlsx', read_only=True)
    ws = wb['Sheet2']

    m = 8211

    i = 0
    for row in ws.rows:
        i += 1
        if i == 1: continue
        if i > m: break

        a = []
        for cell in row:
            k = cell.value
            if k is None or k == 'NULL':
                k = 0

            elif isinstance(k, str) and k.find("'") >= 0:
                k = k.replace("'", "''")

            a.append(k)

        v = ('insert into sales(ims_class1, year, period, cust_code, data, cust_name, clinic, ims_class2, cust_group1, cust_group2, cust_group3, '
            'corporate_group, cust_addr1, cust_addr2, cust_addr3, '
            'postal_code, area, territory, state, manager, detail_man_name, detail_man_code, '
            'zp_item_code, product_group, item_name, sales_unit, bonus_unit, sales_value) values('
            "'{0}', {1}, {2}, '{3}', '{4}', '{5}', '{6}', '{7}', '{8}', '{9}', '{10}', "
            "'{11}', '{12}', '{13}', '{14}', "
            "'{15}', '{16}', '{17}', '{18}', '{19}', '{20}', '{21}', "
            "'{22}', '{23}', '{24}', {25}, {26}, {27})"
        )
        c = v.format(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10],
        a[11], a[12], a[13], a[14],
        a[15], a[16], a[17], a[18], a[19], a[20], a[21],
        a[22], a[23], a[24], a[25], a[26], a[27])
        o.write(c + '\n')

    wb.close()
    o.close()

if __name__ == '__main__':
    readfile()