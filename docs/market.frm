VERSION 5.00
Begin {C62A69F0-16DC-11CE-9E98-00AA00574A4F} MARKET 
   Caption         =   "MARKET"
   ClientHeight    =   4275
   ClientLeft      =   120
   ClientTop       =   460
   ClientWidth     =   9120.001
   OleObjectBlob   =   "market.frx":0000
   StartUpPosition =   1  'CenterOwner
End
Attribute VB_Name = "MARKET"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
'Comentario: formulario exportado de Excel para documentar la interfaz original de CW-DARMM'
Private Sub UserForm_Initialize()
    ' Cambiar el color del texto de un Label dentro del Frame al cargar el formulario
    Label1.ForeColor = RGB(255, 255, 255) ' Blanco
    Label2.ForeColor = RGB(255, 255, 255)
    Label3.ForeColor = RGB(255, 255, 255)
    Label4.ForeColor = RGB(255, 255, 255)
    Label5.ForeColor = RGB(255, 255, 255)
    Label7.ForeColor = RGB(255, 255, 255)

    With MARKET_BOX
    ' Indices
        .AddItem "NASDAQ"
        .AddItem "S&P 500"
        .AddItem "MIDCAP"
        .AddItem "DOW JONES"
        .AddItem "RUSSELL"
    ' Metals
        .AddItem "GOLD"
        .AddItem "SILVER"
        .AddItem "COPPER"
        .AddItem "PLATINUM"
        .AddItem "PALLADIUM"
    ' Energies
        .AddItem "CRUDE OIL"
        .AddItem "HEATING OIL"
        .AddItem "NATURAL GAS"
        .AddItem "BRENT CRUDE"
        .AddItem "R BOB GASOLINE"
    End With
    
    With ACOUNT_BOX
        .AddItem "TRADEIFY"
        .AddItem "TOPSTEP"
        .AddItem "NEXGEN"
        .AddItem "NINJA TRADER"
        .AddItem "TICKTICK TRADER"
        .AddItem "BLUSKY.PRO"
        .AddItem "ONE UP TRADER"
        .AddItem "FUTURES ELITE"
        .AddItem "APEX"
        .AddItem "MY FUNDED FUTURES"
        .AddItem "TAKE PROFIT TRADER"
    End With
End Sub
Private Sub ACOUNT_BOX_Change()
    MARKETDATA_BOX.Clear
    ACOUNT_BOX.BackColor = &H0&    ' Negro
    ACOUNT_BOX.ForeColor = &HFFFFFF ' Blanco
    ' Cambiar las opciones segœn la selecci—n
    Select Case ACOUNT_BOX.Value
        Case "TRADEIFY"
            MARKETDATA_BOX.AddItem "TRADOVATE"
            ACOUNT_BOX.BackColor = RGB(0, 128, 128)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "TOPSTEP"
        MARKET_BOX.Clear
            With MARKET_BOX
            ' Indices
                .AddItem "NASDAQ"
                .AddItem "S&P 500"
                .AddItem "DOW JONES"
                .AddItem "RUSSELL"
            ' Metals
                .AddItem "GOLD"
                .AddItem "SILVER"
                .AddItem "COPPER"
                .AddItem "PLATINUM"
            ' Energies
                .AddItem "CRUDE OIL"
                .AddItem "HEATING OIL"
                .AddItem "NATURAL GAS"
                .AddItem "R BOB GASOLINE"
            End With
            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "RITHMIC"
            MARKETDATA_BOX.AddItem "PROJECTX"
            ACOUNT_BOX.BackColor = &HFFFFFF
            ACOUNT_BOX.ForeColor = &H0&
        Case "NEXGEN"
        MARKET_BOX.Clear
            With MARKET_BOX
            ' Indices
                .AddItem "NASDAQ"
                .AddItem "S&P 500"
                .AddItem "DOW JONES"
                .AddItem "RUSSELL"
            ' Metals
                .AddItem "GOLD"
                .AddItem "SILVER"
                .AddItem "COPPER"
                .AddItem "PLATINUM"
            ' Energies
                .AddItem "CRUDE OIL"
                .AddItem "HEATING OIL"
                .AddItem "NATURAL GAS"
                .AddItem "R BOB GASOLINE"
            End With

            MARKETDATA_BOX.AddItem "RITHMIC"
            MARKETDATA_BOX.AddItem "PROJECTX"
            ACOUNT_BOX.BackColor = RGB(255, 153, 0)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "NINJA TRADER"
            MARKETDATA_BOX.AddItem "PERSONAL"
            ACOUNT_BOX.BackColor = &H0&
            ACOUNT_BOX.ForeColor = RGB(255, 102, 0)
        Case "TICKTICK TRADER"
        MARKET_BOX.Clear
            With MARKET_BOX
            ' Indices
                .AddItem "NASDAQ"
                .AddItem "S&P 500"
                .AddItem "MIDCAP"
                .AddItem "DOW JONES"
                .AddItem "RUSSELL"
            ' Metals
                .AddItem "GOLD"
                .AddItem "SILVER"
                .AddItem "COPPER"
            ' Energies
                .AddItem "CRUDE OIL"
                .AddItem "HEATING OIL"
                .AddItem "NATURAL GAS"
            End With

            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "RITHMIC"
            MARKETDATA_BOX.AddItem "PROJECTX"
            ACOUNT_BOX.BackColor = RGB(0, 255, 255)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "BLUSKY.PRO"
            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "RITHMIC"
            ACOUNT_BOX.BackColor = RGB(100, 149, 237)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "ONE UP TRADER"
        MARKET_BOX.Clear
            With MARKET_BOX
            ' Indices
                .AddItem "NASDAQ"
                .AddItem "S&P 500"
                .AddItem "MIDCAP"
                .AddItem "DOW JONES"
                .AddItem "RUSSELL"
            ' Metals
                .AddItem "GOLD"
                .AddItem "SILVER"
                .AddItem "COPPER"
                .AddItem "PLATINUM"
                .AddItem "PALLADIUM"
            ' Energies
                .AddItem "CRUDE OIL"
                .AddItem "HEATING OIL"
                .AddItem "NATURAL GAS"
                .AddItem "R BOB GASOLINE"
            End With

            MARKETDATA_BOX.AddItem "RITHMIC"
            ACOUNT_BOX.BackColor = RGB(218, 112, 214)
            ACOUNT_BOX.ForeColor = &H0&
        Case "FUTURES ELITE"
            MARKETDATA_BOX.AddItem "PROJECTX"
            MARKETDATA_BOX.AddItem "DXFEED"
            ACOUNT_BOX.BackColor = RGB(128, 0, 128)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "APEX"
        MARKET_BOX.Clear
            With MARKET_BOX
            ' Indices
                .AddItem "NASDAQ"
                .AddItem "S&P 500"
                .AddItem "MIDCAP"
                .AddItem "DOW JONES"
                .AddItem "RUSSELL"
            ' Metals
                .AddItem "GOLD"
                .AddItem "SILVER"
                .AddItem "COPPER"
                .AddItem "PLATINUM"
                .AddItem "PALLADIUM"
            ' Energies
                .AddItem "CRUDE OIL"
                .AddItem "HEATING OIL"
                .AddItem "NATURAL GAS"
                .AddItem "R BOB GASOLINE"
            End With

            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "RITHMIC"
            ACOUNT_BOX.BackColor = RGB(0, 0, 255)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "MY FUNDED FUTURES"
        MARKET_BOX.Clear
            With MARKET_BOX
            ' Indices
                .AddItem "NASDAQ"
                .AddItem "S&P 500"
                .AddItem "DOW JONES"
                .AddItem "RUSSELL"
            ' Metals
                .AddItem "GOLD"
                .AddItem "SILVER"
                .AddItem "COPPER"
                .AddItem "PLATINUM"
            ' Energies
                .AddItem "CRUDE OIL"
                .AddItem "HEATING OIL"
                .AddItem "NATURAL GAS"
                .AddItem "R BOB GASOLINE"
            End With

            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "DXFEED"
            ACOUNT_BOX.BackColor = RGB(218, 165, 32)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "TAKE PROFIT TRADER"
            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "RITHMIC"
            ACOUNT_BOX.BackColor = RGB(34, 139, 34)
            ACOUNT_BOX.ForeColor = &HFFFFFF
    End Select

End Sub
Private Sub MARKET_BOX_Change()
 Select Case MARKET_BOX.Value
    Case "NASDAQ"
        MARKET_BOX.BackColor = RGB(255, 192, 203)
        MARKET_BOX.ForeColor = &H0&
    Case "S&P 500"
        MARKET_BOX.BackColor = RGB(152, 251, 152)
        MARKET_BOX.ForeColor = &H0&
    Case "GOLD"
        MARKET_BOX.BackColor = RGB(218, 165, 32)
        MARKET_BOX.ForeColor = &H0&
    Case "CRUDE OIL"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "DOW JONES"
        MARKET_BOX.BackColor = RGB(68, 199, 231)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "MIDCAP"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "RUSSELL"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "SILVER"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "COPPER"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "HEATING OIL"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "NATURAL GAS"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "BRENT CRUDE"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "R BOB GASOLINE"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "PLATINUM"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
    Case "PALLADIUM"
        MARKET_BOX.BackColor = RGB(0, 0, 0)
        MARKET_BOX.ForeColor = &HFFFFFF
 End Select
 
End Sub
Private Sub AccountSize_Change()
    
    ' Validaciones de entrada
    If Not IsNumeric(AccountSize.Value) Then
        AccountSize.BackColor = RGB(255, 182, 193)
    ElseIf IsNumeric(AccountSize.Value) And AccountSize.Value > 0 Then
        AccountSize.BackColor = &HFFFFFF
    End If

End Sub

Private Sub RiskPercentageA_Change()

    If RiskPercentageA.Value = "" Or RiskPercentageA.Value = 0 Then
        RiskPercentageA.BackColor = RGB(255, 182, 193)
    ElseIf Not IsNumeric(RiskPercentageA.Value) Then
        RiskPercentageA.BackColor = RGB(255, 182, 193)
    ElseIf RiskPercentageA.Value >= 3.5 Then
        RiskPercentageA.BackColor = RGB(255, 0, 0)
        MsgBox "Your risk is to high", vbExclamation
    Else
        RiskPercentageA.BackColor = &HFFFFFF
    End If

End Sub

Private Sub RiskPercentageB_Change()

    If RiskPercentageB.Value = "" Or RiskPercentageB.Value = 0 Then
        RiskPercentageB.BackColor = RGB(255, 182, 193)
    ElseIf Not IsNumeric(RiskPercentageB.Value) Then
        RiskPercentageB.BackColor = RGB(255, 182, 193)
    ElseIf RiskPercentageB.Value >= 3.5 Then
        RiskPercentageB.BackColor = RGB(255, 0, 0)
        MsgBox "Your risk is to high", vbExclamation
    Else
        RiskPercentageB.BackColor = &HFFFFFF
    End If

End Sub

Private Sub OPEN_MARKET_Click()

' INICIALIZAR VARIABLES
    ' Worksheet
    Dim WSRM As Worksheet, WS As Worksheet, WSP As Worksheet
    ' Integer
    Dim MCF As Integer, DR As Integer, i As Integer, RESPUESTA As Integer
    ' Boolean
    Dim B1 As Boolean, B2 As Boolean, B3 As Boolean
    ' String
    Dim CRITERIA_MARKET As String, CRITERIA_ACCOUNT As String, CRITERIA_MARKETDATA As String
    ' Variant
    Dim HOJASBASE As Variant, HOJA As Variant
    ' Long
        'Filas
        Dim LRNSA As Long, LRNSB As Long, LRNSC As Long, LRNSD As Long, LRNSE As Long, LRNSF As Long
        ' Columnas
        Dim LCNS1 As Long, LCNS2 As Long, LCNS3 As Long, LCNS4 As Long, LCNS5 As Long, LCNS6 As Long
        
'INICIALIZAR VARIABLES
    ' Definir criterios segœn el mercado seleccionado, NS (Nombre Sheet)
    Select Case MARKET_BOX.Value
        Case "NASDAQ": CRITERIA_MARKET = "NASDAQ"
        Case "S&P 500": CRITERIA_MARKET = "S&P 500"
        Case "GOLD": CRITERIA_MARKET = "GOLD"
        Case "CRUDE OIL": CRITERIA_MARKET = "CRUDE OIL"
        Case "DOW JONES": CRITERIA_MARKET = "DOW JONES"
        Case "MIDCAP": CRITERIA_MARKET = "MIDCAP"
        Case "RUSSELL": CRITERIA_MARKET = "RUSSELL"
        Case "SILVER": CRITERIA_MARKET = "SILVER"
        Case "COPPER": CRITERIA_MARKET = "COPPER"
        Case "PLATINUM": CRITERIA_MARKET = "PLATINUM"
        Case "PALLADIUM": CRITERIA_MARKET = "PALLADIUM"
        Case "HEATING OIL": CRITERIA_MARKET = "HEATING OIL"
        Case "NATURAL GAS": CRITERIA_MARKET = "NATURAL GAS"
        Case "BRENT CRUDE": CRITERIA_MARKET = "BRENT CRUDE"
        Case "R BOB GASOLINE": CRITERIA_MARKET = "R BOB GASOLINE"
        Case Else
            MsgBox "Select market.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
    End Select

    
    ' Definir criterios segœn el account seleccionado
    Select Case ACOUNT_BOX.Value
        Case "TRADEIFY": CRITERIA_ACCOUNT = "TRADEIFY"
        Case "TOPSTEP": CRITERIA_ACCOUNT = "TOPSTEP"
        Case "NEXGEN": CRITERIA_ACCOUNT = "NEXGEN"
        Case "NINJA TRADER": CRITERIA_ACCOUNT = "NINJA TRADER"
        Case "BLUSKY.PRO": CRITERIA_ACCOUNT = "BLUSKY.PRO"
        Case "ONE UP TRADER": CRITERIA_ACCOUNT = "ONE UP TRADER"
        Case "FUTURES ELITE": CRITERIA_ACCOUNT = "FUTURES ELITE"
        Case "TICKTICK TRADER": CRITERIA_ACCOUNT = "TICKTICK TRADER"
        Case "APEX": CRITERIA_ACCOUNT = "APEX"
        Case "MY FUNDED FUTURES": CRITERIA_ACCOUNT = "MY FUNDED FUTURES"
        Case "TAKE PROFIT TRADER": CRITERIA_ACCOUNT = "TAKE PROFIT TRADER"
        Case Else
            MsgBox "Select account.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
    End Select
        
    ' Definir criterios segœn el activo seleccionado
    Select Case MARKETDATA_BOX.Value
        Case "TRADOVATE": CRITERIA_MARKETDATA = "TRADOVATE"
        Case "RITHMIC": CRITERIA_MARKETDATA = "RITHMIC"
        Case "PROJECTX": CRITERIA_MARKETDATA = "PROJECTX"
        Case "DXFEED": CRITERIA_MARKETDATA = "DXFEED"
        Case "PERSONAL": CRITERIA_MARKETDATA = "PERSONAL"
        Case Else
            MsgBox "Select market data.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
    End Select
    
    ' Account Size
    If AccountSize.Value = "" Then
        AccountSize.BackColor = RGB(255, 182, 193)
        AccountSize.BackColor = RGB(255, 182, 193)
        MsgBox "Input account size.", vbInformation + vbOKOnly, "ERROR"
        Exit Sub
    ElseIf Not IsNumeric(AccountSize.Value) Then
        MsgBox "The account size has to be numeric.", vbCritical + vbOKOnly, "ERROR"
        Exit Sub
    Else
        AccountSize.BackColor = &HFFFFFF
    End If
    
    ' Risk Percentage A
    If RiskPercentageA.Value = "" Or RiskPercentageA.Value = 0 Then
        RiskPercentageA.BackColor = RGB(255, 182, 193)
    ElseIf Not IsNumeric(RiskPercentageA.Value) Then
        RiskPercentageA.BackColor = RGB(255, 182, 193)
    ElseIf RiskPercentageA.Value >= 3.5 Then
        RiskPercentageA.BackColor = RGB(255, 0, 0)
        MsgBox "Your risk is to high", vbExclamation
    Else
        RiskPercentageA.BackColor = &HFFFFFF
    End If
    
    ' Risk Percentage B
    If RiskPercentageB.Value = "" Or RiskPercentageB.Value = 0 Then
        RiskPercentageB.BackColor = RGB(255, 182, 193)
    ElseIf Not IsNumeric(RiskPercentageB.Value) Then
        RiskPercentageB.BackColor = RGB(255, 182, 193)
    ElseIf RiskPercentageB.Value >= 3.5 Then
        RiskPercentageB.BackColor = RGB(255, 0, 0)
        MsgBox "Your risk is to high", vbExclamation
    Else
        RiskPercentageB.BackColor = &HFFFFFF
    End If
    
    
' ASIGNAR HOJAS DE TRABAJO
    '(Worksheets Markets)
    Set WSRM = Worksheets("MARKETS")
    '(Worksheets Plantilla)
    Set WSP = Worksheets("PLANTILLA")

' FILAS
    'Ultima fila de la columna A (Last Row Market)
    LRMA = WSRM.Cells(WSRM.Rows.Count, "A").End(xlUp).ROW
    'Ultima fila de la columna B (Acount)
    LRMB = WSRM.Cells(WSRM.Rows.Count, "B").End(xlUp).ROW
    'Ultima fila de la columna C (Market Data)
    LRMC = WSRM.Cells(WSRM.Rows.Count, "C").End(xlUp).ROW
    'Ultima fila de la columna D (Account Size Initial)
    LRMD = WSRM.Cells(WSRM.Rows.Count, "D").End(xlUp).ROW
    'Ultima fila de la columna E (Risk Percentage Initia A)
    LRME = WSRM.Cells(WSRM.Rows.Count, "E").End(xlUp).ROW
    'Ultima fila de la columna F (Risk Percentage Initia B)
    LRMF = WSRM.Cells(WSRM.Rows.Count, "F").End(xlUp).ROW

' COLUMNAS
    'Fecha (5B) (Market Column Fecha)
    MCF = WSRM.Cells(5, Columns.Count).End(xlToLeft).Column

' BANDERAS
    B1 = True
    
' FECHA
    'Dice el d’a de la fecha y el DR (D’as Restantes)
    Select Case Weekday(Date)
        Case 2: DR = 6
        Case 3: DR = 5
        Case 4: DR = 4
        Case 5: DR = 3
        Case 6: DR = 2
        Case Else
            DR = 0
            MsgBox "FIN DE SEMANA", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
    End Select
    
    
' LIMPIEZA COMPLETA DE LA HOJA MARKETS Y REVISIîN DE DêAS
    If DR = 0 Then
        'Tabla de markets y acounts
        WSRM.Rows("7:" & WSRM.Rows.Count).Clear
        'Fecha
        WSRM.Range(WSRM.Cells(5, 2), WSRM.Cells(5, MCF)).Clear
        
        'Elimina hojas de otra semana
        
        ' Hojas que no se eliminar‡n
        HOJASBASE = Array("RESULTS", "BD_MARKET", "MARKETS", "PLANTILLA")
        ' Recorremos todas las hojas
        Application.DisplayAlerts = False ' Desactivar alertas para evitar mensajes de confirmaci—n
        For Each WS In ThisWorkbook.Worksheets
            ' Comprobamos si la hoja no est‡ en la lista de hojas excluidas
            If IsError(Application.Match(WS.Name, HOJASBASE, 0)) Then
                WS.Delete
            End If
        Next WS
        Application.DisplayAlerts = True ' Restaurar alertas
    
        Else
            'Cambia la fecha por d’a y solo pueden existir # fechas diferentes dependiendo el d’a que inicie
            If Date <> WSRM.Cells(5, MCF) Then
                WSRM.Cells(5, MCF) = Date
            End If
        End If


' LLENAR BD MARKET REVISANDO QUE NO HAYA DUIPLICADOS
B3 = True
    For i = 1 To Application.WorksheetFunction.Min(LRMA, LRMB, LRMC)
        ' Comprobar si hay coincidencia en las tres columnas
        If WSRM.Cells(i, 1).Value = CRITERIA_MARKET And _
           WSRM.Cells(i, 2).Value = CRITERIA_ACCOUNT And _
           WSRM.Cells(i, 3).Value = CRITERIA_MARKETDATA Then
            ' Si se encuentran coincidencias, mostrar mensaje
            RESPUESTA = MsgBox("Error, se repite la informaci—n. ÀDesea reemplazar los datos?", vbYesNo + vbExclamation, "Reemplazar datos")
            B3 = False
            If RESPUESTA = vbNo Then
                ' Si el usuario elige NO, salir del Sub
                Exit Sub
            Else
                ' Si el usuario elige Sê, reemplazar los datos
                WSRM.Cells(i, 4).Value = AccountSize.Value
                WSRM.Cells(i, 5).Value = RiskPercentageA.Value
                WSRM.Cells(i, 6).Value = RiskPercentageB.Value
            End If
        End If
    Next i
    
    If B3 = True Then
        ' Si no se encontraron coincidencias, insertar nuevos datos
        WSRM.Cells(LRMA + 1, 1).Value = CRITERIA_MARKET
        WSRM.Cells(LRMB + 1, 2).Value = CRITERIA_ACCOUNT
        WSRM.Cells(LRMC + 1, 3).Value = CRITERIA_MARKETDATA
        WSRM.Cells(LRMD + 1, 4).Value = AccountSize.Value
        WSRM.Cells(LRME + 1, 5).Value = RiskPercentageA.Value
        WSRM.Cells(LRMF + 1, 6).Value = RiskPercentageB.Value
    End If


    
' CREAR HOJA CRITERIA_MARKET
    ' Revisa si ya existe la hoja
    For Each WS In ThisWorkbook.Worksheets
        If WS.Name = CRITERIA_MARKET Then
            B2 = True
            Exit For
        End If
    Next WS
    
    ' Si la hoja no existe, crearla
    If Not B2 Then
        ' Crea la hoja MARKET
        Sheets.Add(Before:=Sheets(2)).Name = CRITERIA_MARKET
        Set NS = Worksheets(CRITERIA_MARKET)
        
        ' Formato de la hoja
        ' Copiar el formato de la hoja "PLATILLA"
        WSP.Cells.Copy
        Worksheets(CRITERIA_MARKET).Cells.PasteSpecial Paste:=xlPasteValuesAndNumberFormats ' Pega los valores y formatos
        Worksheets(CRITERIA_MARKET).Cells.PasteSpecial Paste:=xlPasteFormats  ' Pega el formato de nuevo, si lo necesitas
        Worksheets(CRITERIA_MARKET).Cells(11, 2) = Date
        ' Copiar controles (botones, im‡genes, etc.) de la hoja origen
        ' Copiar las formas (botones, im‡genes, etc.) de la hoja origen
        For Each shp In WSP.Shapes
            shp.Copy
            Worksheets(CRITERIA_MARKET).Paste
        Next shp
        ' Limpiar el portapapeles para evitar el mensaje de "ÀDeseas mantener los datos copiados?"
        Application.CutCopyMode = False

    End If
    
    ' Ultima columna de las filas de datos en la hoja de CRITERIA_MARKET
    LCNS1 = Worksheets(CRITERIA_MARKET).Cells(1, Columns.Count).End(xlToLeft).Column
    LCNS2 = Worksheets(CRITERIA_MARKET).Cells(2, Columns.Count).End(xlToLeft).Column
    LCNS3 = Worksheets(CRITERIA_MARKET).Cells(3, Columns.Count).End(xlToLeft).Column
    LCNS4 = Worksheets(CRITERIA_MARKET).Cells(4, Columns.Count).End(xlToLeft).Column
    LCNS5 = Worksheets(CRITERIA_MARKET).Cells(5, Columns.Count).End(xlToLeft).Column
    LCNS6 = Worksheets(CRITERIA_MARKET).Cells(6, Columns.Count).End(xlToLeft).Column
    
    ' Ultima fila de las columnas en la hoja de CRITERIA_MARKET
    LRNSA = Worksheets(CRITERIA_MARKET).Cells(Worksheets(CRITERIA_MARKET).Rows.Count, "A").End(xlUp).ROW
    LRNSB = Worksheets(CRITERIA_MARKET).Cells(Worksheets(CRITERIA_MARKET).Rows.Count, "B").End(xlUp).ROW
    LRNSC = Worksheets(CRITERIA_MARKET).Cells(Worksheets(CRITERIA_MARKET).Rows.Count, "C").End(xlUp).ROW
    LRNSD = Worksheets(CRITERIA_MARKET).Cells(Worksheets(CRITERIA_MARKET).Rows.Count, "D").End(xlUp).ROW
    LRNSE = Worksheets(CRITERIA_MARKET).Cells(Worksheets(CRITERIA_MARKET).Rows.Count, "E").End(xlUp).ROW
    LRNSF = Worksheets(CRITERIA_MARKET).Cells(Worksheets(CRITERIA_MARKET).Rows.Count, "F").End(xlUp).ROW
    LRNSG = Worksheets(CRITERIA_MARKET).Cells(Worksheets(CRITERIA_MARKET).Rows.Count, "G").End(xlUp).ROW
    
    ' Comprobar si ya existen los mismos valores en la hoja de CRITERIA_MARKET  inicio
    For i = 1 To LCNS1
        ' Comprobar si ya existe el mismo criterio (Market, Account, MarketData)
        If Worksheets(CRITERIA_MARKET).Cells(1, i).Value = CRITERIA_MARKET And _
           Worksheets(CRITERIA_MARKET).Cells(2, i).Value = CRITERIA_ACCOUNT And _
           Worksheets(CRITERIA_MARKET).Cells(3, i).Value = CRITERIA_MARKETDATA Then
       
            If RESPUESTA = vbNo Then
                ' Si el usuario elige NO, salir del Sub
                Exit Sub
            Else
                ' Si el usuario elige Sê, reemplazar los datos en la hoja CRITERIA_MARKET
                Worksheets(CRITERIA_MARKET).Cells(4, i).Value = AccountSize.Value
                Worksheets(CRITERIA_MARKET).Cells(5, i).Value = RiskPercentageA.Value
                Worksheets(CRITERIA_MARKET).Cells(6, i).Value = RiskPercentageB.Value
                
                ' Seleccionar todo el rango de celdas en la hoja
                Worksheets(CRITERIA_MARKET).Cells.HorizontalAlignment = xlCenter
                Worksheets(CRITERIA_MARKET).Cells.VerticalAlignment = xlCenter
                
            End If
        End If
    Next i
    
    ' Comprobar si ya existen los mismos valores en la hoja de CRITERIA_MARKET
    For i = 1 To LRNSA
        ' Comprobar si ya existe el mismo criterio (Market, Account, MarketData)
        If Worksheets(CRITERIA_MARKET).Cells(i, 3).Value = CRITERIA_ACCOUNT And _
           Worksheets(CRITERIA_MARKET).Cells(i, 4).Value = CRITERIA_MARKETDATA Then
                      
            If RESPUESTA = vbNo Then
                ' Si el usuario elige NO, salir del Sub
                Exit Sub
            Else
                ' Si el usuario elige Sê, reemplazar los datos en la hoja CRITERIA_MARKET
                Worksheets(CRITERIA_MARKET).Cells(i, 5).Value = AccountSize.Value
                Worksheets(CRITERIA_MARKET).Cells(i, 6).Value = RiskPercentageA.Value
                Worksheets(CRITERIA_MARKET).Cells(i, 7).Value = RiskPercentageB.Value
                
            End If
        End If
    Next i
    
    If B3 = True Then
        ' Si no se encontraron coincidencias, insertar nuevos datos en la hoja CRITERIA_MARKET inicio
        Worksheets(CRITERIA_MARKET).Cells(1, LCNS1 + 1).Value = CRITERIA_MARKET
        Worksheets(CRITERIA_MARKET).Cells(2, LCNS2 + 1).Value = CRITERIA_ACCOUNT
        Worksheets(CRITERIA_MARKET).Cells(3, LCNS3 + 1).Value = CRITERIA_MARKETDATA
        Worksheets(CRITERIA_MARKET).Cells(4, LCNS4 + 1).Value = AccountSize.Value
        Worksheets(CRITERIA_MARKET).Cells(5, LCNS5 + 1).Value = RiskPercentageA.Value
        Worksheets(CRITERIA_MARKET).Cells(6, LCNS6 + 1).Value = RiskPercentageB.Value
           
         ' Si no se encontraron coincidencias, insertar nuevos datos en la hoja CRITERIA_MARKET
        Worksheets(CRITERIA_MARKET).Cells(LRNSA + 1, 1).Value = 0
        Worksheets(CRITERIA_MARKET).Cells(LRNSB + 1, 2).Value = "INITIAL"
        Worksheets(CRITERIA_MARKET).Cells(LRNSC + 1, 3).Value = CRITERIA_ACCOUNT
        Worksheets(CRITERIA_MARKET).Cells(LRNSD + 1, 4).Value = CRITERIA_MARKETDATA
        Worksheets(CRITERIA_MARKET).Cells(LRNSE + 1, 5).Value = AccountSize.Value
        Worksheets(CRITERIA_MARKET).Cells(LRNSF + 1, 6).Value = RiskPercentageA.Value
        Worksheets(CRITERIA_MARKET).Cells(LRNSG + 1, 7).Value = RiskPercentageB.Value
    End If
    
    ' Seleccionar todo el rango de celdas en la hoja
    Worksheets(CRITERIA_MARKET).Cells.HorizontalAlignment = xlCenter
    Worksheets(CRITERIA_MARKET).Cells.VerticalAlignment = xlCenter
    
End Sub

Private Sub CommandButton1_Click()
MARKET.Hide
Unload Me
End Sub
