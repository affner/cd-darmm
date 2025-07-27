VERSION 5.00
Begin {C62A69F0-16DC-11CE-9E98-00AA00574A4F} RISK_MANAGER 
   Caption         =   "RISK MANAGER "
   ClientHeight    =   5760
   ClientLeft      =   40
   ClientTop       =   400
   ClientWidth     =   9860.001
   OleObjectBlob   =   "risk_market.frx":0000
   StartUpPosition =   1  'CenterOwner
End
Attribute VB_Name = "RISK_MANAGER"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
'Autor: Jassive Jazareth Ramon Rosas
'Fecha: 04-03-2025
'Nota: este formulario se exportó para documentar la lógica de riesgo del Excel y se usa como referencia para la implementación en Java'
Private Sub UserForm_Initialize()
    
    Dim WS As Worksheet
    Set WS = ActiveSheet
    
    'Ultima fila de la columna A (Last Row Market)
    LRA = WS.Cells(WS.Rows.Count, "A").End(xlUp).ROW
    
    ' Cambiar el color del texto de un Label dentro del Frame al cargar el formulario
    Label1.ForeColor = RGB(255, 255, 255) ' Blanco
    Label3.ForeColor = RGB(255, 255, 255)
    Label4.ForeColor = RGB(255, 255, 255)
    Label5.ForeColor = RGB(255, 255, 255)
    Label6.ForeColor = RGB(255, 255, 255)
    Label7.ForeColor = RGB(255, 255, 255)
    stop_ticks.ForeColor = RGB(255, 255, 255)

    With MARKET_BOX
        Select Case WS.Cells(1, 2).Value
            Case "NASDAQ", "S&P 500", "GOLD", "CRUDE OIL", "DOW JONES", "MIDCAP", "RUSSELL", _
                 "SILVER", "COPPER", "HEATING OIL", "NATURAL GAS", "BRENT CRUDE", "R BOB GASOLINE", _
                 "PLATINUM", "PALLADIUM"
                .AddItem WS.Cells(1, 2).Value
        End Select
    End With
    
    Dim itemExists As Boolean
    For i = 2 To LRA
        With ACOUNT_BOX
            ' Reset itemExists to False for each iteration
            itemExists = False
            
            ' Check if the item already exists in the ComboBox
            For J = 0 To .ListCount - 1
                If .List(J) = WS.Cells(2, i).Value Then
                    itemExists = True
                    Exit For
                End If
            Next J
            
            ' If the item doesn't exist, add it
            If Not itemExists Then
                Select Case WS.Cells(2, i).Value
                    Case "TRADEIFY", "TOPSTEP", "NEXGEN", "NINJA TRADER", "TICKTICK TRADER", "BLUSKY.PRO", _
                         "ONE UP TRADER", "FUTURES ELITE", "APEX", "MY FUNDED FUTURES", "TAKE PROFIT TRADER"
                        .AddItem WS.Cells(2, i).Value
                End Select
            End If
        End With
    Next i


End Sub
Private Sub AccountSize_Change()
    
    ' Validaciones de entrada
    If Not IsNumeric(AccountSize.Value) Then
        AccountSize.BackColor = RGB(255, 182, 193)
    ElseIf IsNumeric(AccountSize.Value) And AccountSize.Value > 0 Then
        AccountSize.BackColor = &HFFFFFF
    End If

End Sub
Private Sub RISKTOREWARD_Change()
    If RISKTOREWARD.Value = "" Or RISKTOREWARD.Value = 0 Then
        RISKTOREWARD.BackColor = RGB(255, 182, 193)
    ElseIf Not IsNumeric(RISKTOREWARD.Value) Then
        RISKTOREWARD.BackColor = RGB(255, 182, 193)
    Else
        RISKTOREWARD.BackColor = &HFFFFFF
    End If
End Sub
Private Sub StopTickIni_Change()
    Dim valor As Variant
    If Not IsNumeric(StopTickIni.Value) Then
        StopTickIni.BackColor = RGB(255, 182, 193)
    Else
        ' Convertir el valor a nœmero
        valor = CDbl(StopTickIni.Value)
        If valor = Int(valor) Then
            StopTickIni.BackColor = &HFFFFFF
        Else
            StopTickIni.BackColor = RGB(255, 182, 193)
            MsgBox "The stop tick initial has to be integer.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        End If
    End If
End Sub

Private Sub StopTickFin_Change()
    If Not IsNumeric(StopTickFin.Value) Then
        StopTickFin.BackColor = RGB(255, 182, 193)
    Else
        ' Convertir el valor a nœmero
        valor = CDbl(StopTickFin.Value)
        If valor = Int(valor) Then
            StopTickFin.BackColor = &HFFFFFF
        Else
            StopTickFin.BackColor = RGB(255, 182, 193)
            MsgBox "The stop tick final has to be integer.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        End If
    End If
End Sub
Private Sub StopTickStep_Change()
    If Not IsNumeric(StopTickStep.Value) Then
        StopTickStep.BackColor = RGB(255, 182, 193)
    Else
        ' Convertir el valor a nœmero
        valor = CDbl(StopTickStep.Value)
        If valor = Int(valor) Then
            StopTickStep.BackColor = &HFFFFFF
        Else
            StopTickStep.BackColor = RGB(255, 182, 193)
            MsgBox "The stop tick step has to be integer.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        End If
    End If
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
Private Sub ACOUNT_BOX_Change()
    Dim WS As Worksheet
    Set WS = ActiveSheet
    
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
                Select Case WS.Cells(1, 2).Value
                    Case "NASDAQ", "S&P 500", "GOLD", "CRUDE OIL", "DOW JONES", "RUSSELL", _
                         "SILVER", "COPPER", "HEATING OIL", "NATURAL GAS", "R BOB GASOLINE", _
                         "PLATINUM"
                        .AddItem WS.Cells(1, 2).Value
                End Select
            End With
            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "RITHMIC"
            MARKETDATA_BOX.AddItem "PROJECTX"
            ACOUNT_BOX.BackColor = &HFFFFFF
            ACOUNT_BOX.ForeColor = &H0&
        Case "NEXGEN"
        MARKET_BOX.Clear
            With MARKET_BOX
                Select Case WS.Cells(1, 2).Value
                    Case "NASDAQ", "S&P 500", "GOLD", "CRUDE OIL", "DOW JONES", "RUSSELL", _
                         "SILVER", "COPPER", "HEATING OIL", "NATURAL GAS", "R BOB GASOLINE", _
                         "PLATINUM"
                        .AddItem WS.Cells(1, 2).Value
                End Select
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
                Select Case WS.Cells(1, 2).Value
                    Case "NASDAQ", "S&P 500", "GOLD", "CRUDE OIL", "DOW JONES", "MIDCAP", "RUSSELL", _
                         "SILVER", "COPPER", "HEATING OIL", "NATURAL GAS"
                        .AddItem WS.Cells(1, 2).Value
                End Select
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
                Select Case WS.Cells(1, 2).Value
                    Case "NASDAQ", "S&P 500", "GOLD", "CRUDE OIL", "DOW JONES", "MIDCAP", "RUSSELL", _
                         "SILVER", "COPPER", "HEATING OIL", "NATURAL GAS", "R BOB GASOLINE", _
                         "PLATINUM", "PALLADIUM"
                        .AddItem WS.Cells(1, 2).Value
                End Select
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
                Select Case WS.Cells(1, 2).Value
                    Case "NASDAQ", "S&P 500", "GOLD", "CRUDE OIL", "DOW JONES", "MIDCAP", "RUSSELL", _
                         "SILVER", "COPPER", "HEATING OIL", "NATURAL GAS", "R BOB GASOLINE", _
                         "PLATINUM", "PALLADIUM"
                        .AddItem WS.Cells(1, 2).Value
                End Select
            End With
                
            MARKETDATA_BOX.AddItem "TRADOVATE"
            MARKETDATA_BOX.AddItem "RITHMIC"
            ACOUNT_BOX.BackColor = RGB(0, 0, 255)
            ACOUNT_BOX.ForeColor = &HFFFFFF
        Case "MY FUNDED FUTURES"
        MARKET_BOX.Clear
            With MARKET_BOX
                Select Case WS.Cells(1, 2).Value
                    Case "NASDAQ", "S&P 500", "GOLD", "CRUDE OIL", "DOW JONES", "RUSSELL", _
                         "SILVER", "COPPER", "HEATING OIL", "NATURAL GAS", "R BOB GASOLINE", _
                         "PLATINUM", "PALLADIUM"
                        .AddItem WS.Cells(1, 2).Value
                End Select
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
Private Sub HOUSE_Click()
If LUNCH = True Then
    HOUSE = False
End If
End Sub

Private Sub LUNCH_Click()
If HOUSE = True Then
    LUNCH = False
End If
End Sub

Private Sub WIN_Click()
If LOSS = True Then
    WIN = False
End If
End Sub

Private Sub LOSS_Click()
If WIN = True Then
    LOSS = False
End If
End Sub


Private Sub OptimalContracts_Click()
    
    ' Variables de tipo Integer
    Dim OptimalContracts As Integer, target As Integer, maxProfit As Integer, k As Integer, p As Integer, r As Integer
    Dim J As Long, h As Long, size As Integer, PREGUNTA1 As Integer, pregunta2 As Integer, PREGUNTA As Integer, PREGUNTA4 As Integer, PREGUNTA3 As Integer, PREGUNTA5 As Integer
    
    
    ' Variables de tipo Double
    Dim riskPerContract As Double, totalCapitalUsed As Double, realRisk As Double, potentialProfit As Double, revenue As Double
    Dim riskAmount As Double, RiskPercentage_ As Double, TickSize As Double, tickValue As Double, Comission As Double
    
    ' Variables de tipo String
    Dim result As String, Asset_Symbol As String, Broker As String
    Dim Symbol As String, fileName As String, rutaAchivo As String
    Dim CRITERIA_BROKER As String, FirstLetter As String
    
    ' Variables de tipo Long
    Dim LastRow As Long, rowIndex As Long, ROW As Long, currentRow As Long
    Dim maxProfitRow As Long, currentStopTickGroupStart As Long, groupValue As Long, COLOR1 As Long, COLOR2 As Long
    
    ' Variables de tipo Boolean
    Dim bandera As Boolean, B1 As Boolean
    
    ' Variables de tipo Variant
    Dim STOP_TICKS_VALUES As Variant, currentStopTick As Variant, o As Variant, key As Variant
    Dim items As FileDialogSelectedItems
    
    ' Variables de tipo Object
    Dim xmlDoc As Object, root As Object, atmStrategy As Object, brackets As Object, bracket As Object, configurations As Object
    Dim archivo As Object, fso As Object, fd As FileDialog
    
    ' Variables de tipo Date
    Dim fechaCreacion As Date, fechaHoy As Date
    
    ' Variables de tipo Range
    Dim rangoOrigen As Range, rangoDestino As Range
    
    ' Variables de tipo Worksheet
    Dim WS As Worksheet, WSR As Worksheet, WSA As Worksheet


     ' Asignaci—n de hojas de trabajo
    Set WS = Worksheets("BD_MARKET")
    Set WSR = Worksheets("RESULTS")
    Set WSA = ActiveSheet
    
    ' Limpieza de hojas de resultados
    WSR.Rows("2:" & WSR.Rows.Count).Clear

'INICIALIZAR VARIABLES
    ' Definir criterios segœn el mercado seleccionado, NS (Nombre Sheet)
    Select Case MARKET_BOX.Value
        Case "NASDAQ": CRITERIA_MARKET = "NASDAQ": r = 2: BEI = "NQ"
        Case "S&P 500": CRITERIA_MARKET = "S&P 500": r = 1: BEI = "ES"
        Case "GOLD": CRITERIA_MARKET = "GOLD": r = 1: BEI = "GC"
        Case "CRUDE OIL": CRITERIA_MARKET = "CRUDE OIL": r = 1: BEI = "CL"
        Case "DOW JONES": CRITERIA_MARKET = "DOW JONES": r = 1: BEI = "YM"
        Case "MIDCAP": CRITERIA_MARKET = "MIDCAP": r = 1: BEI = "EMD"
        Case "RUSSELL": CRITERIA_MARKET = "RUSSELL": r = 1: BEI = "RTY"
        Case "SILVER": CRITERIA_MARKET = "SILVER": r = 1: BEI = "QI"
        Case "COPPER": CRITERIA_MARKET = "COPPER": r = 1: BEI = "QC"
        Case "PLATINUM": CRITERIA_MARKET = "PLATINUM": r = 1: BEI = "PL"
        Case "PALLADIUM": CRITERIA_MARKET = "PALLADIUM": r = 1: BEI = "PA"
        Case "HEATING OIL": CRITERIA_MARKET = "HEATING OIL": r = 1
        Case "NATURAL GAS": CRITERIA_MARKET = "NATURAL GAS": r = 1
        Case "BRENT CRUDE": CRITERIA_MARKET = "BRENT CRUDE": r = 1
        Case "R BOB GASOLINE": CRITERIA_MARKET = "R BOB GASOLINE": r = 1
        Case Else
            MsgBox "Select market.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
    End Select

    
    ' Definir criterios segœn el account seleccionado
    Select Case ACOUNT_BOX.Value
        Case "TRADEIFY": CRITERIA_ACCOUNT = "TRADEIFY": Ini = "TRA"
        Case "TOPSTEP": CRITERIA_ACCOUNT = "TOPSTEP": Ini = "TOP"
        Case "NEXGEN": CRITERIA_ACCOUNT = "NEXGEN": Ini = "NEX"
        Case "NINJA TRADER": CRITERIA_ACCOUNT = "NINJA TRADER": Ini = "PER"
        Case "BLUSKY.PRO": CRITERIA_ACCOUNT = "BLUSKY.PRO": Ini = "BLU"
        Case "ONE UP TRADER": CRITERIA_ACCOUNT = "ONE UP TRADER": Ini = "ONE"
        Case "FUTURES ELITE": CRITERIA_ACCOUNT = "FUTURES ELITE": Ini = "FUT"
        Case "TICKTICK TRADER": CRITERIA_ACCOUNT = "TICKTICK TRADER": Ini = "TIC"
        Case "APEX": CRITERIA_ACCOUNT = "APEX": Ini = "APE"
        Case "MY FUNDED FUTURES": CRITERIA_ACCOUNT = "MY FUNDED FUTURES": Ini = "MFF"
        Case "TAKE PROFIT TRADER": CRITERIA_ACCOUNT = "TAKE PROFIT TRADER": Ini = "TPT"
        Case Else
            MsgBox "Select account.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
    End Select
            
    ' Definir criterios segœn el activo seleccionado
    Select Case MARKETDATA_BOX.Value
        Case "TRADOVATE": CRITERIA_MARKETDATA = "TRADOVATE": MDT = "TVT"
        Case "RITHMIC": CRITERIA_MARKETDATA = "RITHMIC": MDT = "RT"
        Case "PROJECTX": CRITERIA_MARKETDATA = "PROJECTX": MDT = "PJX"
        Case "DXFEED": CRITERIA_MARKETDATA = "DXFEED": MDT = "DXF"
        Case "PERSONAL": CRITERIA_MARKETDATA = "PERSONAL": MDT = "PR"
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
    
    'Risk to Reward
    If RISKTOREWARD.Value = "" Or RISKTOREWARD.Value = 0 Then
        RISKTOREWARD.BackColor = RGB(255, 182, 193)
        MsgBox "Input risk to reward ratio.", vbInformation + vbOKOnly, "ERROR"
        Exit Sub
    ElseIf Not IsNumeric(RISKTOREWARD.Value) Then
        RISKTOREWARD.BackColor = RGB(255, 182, 193)
        MsgBox "The risk to reward has to be numeric.", vbCritical + vbOKOnly, "ERROR"
        Exit Sub
    Else
        RISKTOREWARD.BackColor = &HFFFFFF
    End If

    ' Configuraci—n de rango de ticks segœn el activo
    If StopTickIni.Value = "" And StopTickFin.Value = "" And StopTickStep.Value = "" Then
        Select Case MARKET_BOX.Value
            Case "NASDAQ": STOP_TICKS_VALUES = Array(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250)
            Case "S&P 500": STOP_TICKS_VALUES = Array(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
            Case Else: STOP_TICKS_VALUES = Array(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50)
        End Select
        StopTickIni.BackColor = &HFFFFFF
        StopTickFin.BackColor = &HFFFFFF
    ElseIf Not IsNumeric(StopTickIni.Value) Then
        StopTickFin.BackColor = RGB(255, 182, 193)
        MsgBox "The stop tick has to be numeric.", vbCritical + vbOKOnly, "ERROR"
        Exit Sub
    ElseIf Not IsNumeric(StopTickFin.Value) Then
        StopTickIni.BackColor = RGB(255, 182, 193)
        MsgBox "The stop tick has to be numeric.", vbCritical + vbOKOnly, "ERROR"
        Exit Sub
    ElseIf Not IsNumeric(StopTickStep.Value) Then
        StopTickStep.BackColor = RGB(255, 182, 193)
        MsgBox "The stop tick step has to be numeric.", vbCritical + vbOKOnly, "ERROR"
        Exit Sub
    Else
        ' Convierte los valores a numŽricos antes de hacer la comparaci—n
        Dim stopIni As Long
        Dim stopFin As Long
        stopIni = CLng(StopTickIni.Value)
        stopFin = CLng(StopTickFin.Value)
        StopStep = CLng(StopTickStep.Value)
    
        If stopIni >= stopFin Then
            StopTickIni.BackColor = RGB(255, 182, 193)
            MsgBox "The initial stop tick must be lower than the final.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        ElseIf StopTickIni.Value = "" And StopTickFin.Value <> "" Then
            StopTickIni.BackColor = RGB(255, 182, 193)
            MsgBox "Input the initial stop tick.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        ElseIf StopTickIni.Value <> "" And StopTickFin.Value = "" Then
            StopTickIni.BackColor = RGB(255, 182, 193)
            MsgBox "Input the final stop tick.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        ElseIf StopTickStep = "" Then
            StopTickStep.BackColor = RGB(255, 182, 193)
            MsgBox "Input the step for the stop tick.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        ElseIf CDbl(StopTickIni.Value) <> Int(CDbl(StopTickIni.Value)) Or CDbl(StopTickFin.Value) <> Int(CDbl(StopTickFin.Value)) Or CDbl(StopTickStep.Value) <> Int(CDbl(StopTickStep.Value)) Then
            MsgBox "The stop tick has to be integer.", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        Else
            Set ListaValor = New Collection
            For i = stopIni To stopFin Step StopStep
                ListaValor.Add i
            Next i
            
            ' Convertir la colecci—n en un array
            ReDim STOP_TICKS_VALUES(1 To ListaValor.Count)
            For i = 1 To ListaValor.Count
                STOP_TICKS_VALUES(i) = ListaValor(i)
            Next i
            
            StopTickIni.BackColor = &HFFFFFF
            StopTickFin.BackColor = &HFFFFFF
        End If
    End If
    
    
' FILAS
    'Ultima fila de la columna A (# Trade)
    LRNSA = WSA.Cells(WSA.Rows.Count, "A").End(xlUp).ROW
    'Ultima fila de la columna B (W/L)
    LRNSB = WSA.Cells(WSA.Rows.Count, "B").End(xlUp).ROW
    'Ultima fila de la columna C (Acount)
    LRNSC = WSA.Cells(WSA.Rows.Count, "C").End(xlUp).ROW
    'Ultima fila de la columna D (Market Data)
    LRNSD = WSA.Cells(WSA.Rows.Count, "D").End(xlUp).ROW
    'Ultima fila de la columna E (Account Size Initial)
    LRNSE = WSA.Cells(WSA.Rows.Count, "E").End(xlUp).ROW
    'Ultima fila de la columna F (Risk Percentage Initia A)
    LRNSF = WSA.Cells(WSA.Rows.Count, "F").End(xlUp).ROW
    'Ultima fila de la columna G (Risk Percentage Initia B)
    LRNSG = WSA.Cells(WSA.Rows.Count, "G").End(xlUp).ROW

' COLUMNAS
    'Ultima columna de la fila 12 para pegar los contratos optimos
    LCNS12 = WSA.Cells(12, Columns.Count).End(xlToLeft).Column
    
    
    ' Configuraci—n de calculo si es House o Lunch
    If HOUSE = True Then
        Q = "H"
        U = LRNSF
    ElseIf LUNCH = True Then
        Q = "L"
        U = LRNSG
    ElseIf HOUSE = False And LUNCH = False Then
        MsgBox "Selecciona si es house o lunch", vbCritical + vbOKOnly, "ERROR"
        Exit Sub
    End If
    
B1 = True
'TRADE #0
    'BUSQUEDA EN LA COLUMNA A, SI HAY UN VALOR <>0
    For i = LRNSA To 12 Step -1 ' Recorre hacia arriba
        ' Buscar el mismo criterio (Account, MarketData)
        If WSA.Cells(i, 3).Value = CRITERIA_ACCOUNT And _
           WSA.Cells(i, 4).Value = CRITERIA_MARKETDATA Then
            
            ' Verificar si el valor en la celda de la columna A es 0
            If WSA.Cells(i, 1).Value = 0 Then
                PREGUNTA5 = MsgBox("Es tu primer trade?", vbYesNo + vbQuestion)
                If PREGUNTA5 = vbYes Then
                    AccountSize.Value = WSA.Cells(i, 5).Value
                    If HOUSE = True Then
                        RiskPercentage = WSA.Cells(i, 6).Value
                    ElseIf LUNCH = True Then
                        RiskPercentage = WSA.Cells(i, 7).Value
                    End If
                    B1 = False
                End If
            End If
            
            Exit For ' Una vez que encuentre la œltima coincidencia, salimos del bucle
        End If
    Next i
    
'LLENADO DE BD
    'SI B1=TRUE ENTONCES NO ES EL TRADE 0
    If B1 = True Then
        ' Configuraci—n de Win o Loss
        If WIN = True Then
            WSA.Cells(LRNSB + 1, 2).Value = "WIN"
            WSA.Cells(LRNSB + 1, 2).Interior.Color = RGB(146, 208, 80)
            risk = 1.05
        ElseIf LOSS = True Then
            WSA.Cells(LRNSB + 1, 2).Value = "LOSS"
            WSA.Cells(LRNSB + 1, 2).Interior.Color = RGB(255, 0, 0)
            risk = 0.98
        ElseIf WIN = False And LOSS = False Then
            MsgBox "Selecciona si es win o loss", vbCritical + vbOKOnly, "ERROR"
            Exit Sub
        End If
        
        ' Acount
        WSA.Cells(LRNSA + 1, 3).Value = CRITERIA_ACCOUNT
        ' Market Data
        WSA.Cells(LRNSA + 1, 4).Value = CRITERIA_MARKETDATA
        ' Account Size
        WSA.Cells(LRNSA + 1, 5).Value = AccountSize.Value
    
        'BUSQUEDA DE LA ULTIMA FILA PARA GUARDAR LOS DATOS (RISK%)
        For i = U To 12 Step -1 ' Recorre hacia arriba
            ' Buscar el mismo criterio (Account, MarketData)
            If WSA.Cells(i, 3).Value = CRITERIA_ACCOUNT And _
               WSA.Cells(i, 4).Value = CRITERIA_MARKETDATA Then
        
                If HOUSE = True Then
                    ' Comprobar que la celda (i,6) no estŽ vac’a
                    Do While IsEmpty(WSA.Cells(i, 6).Value) And i > 1
                        i = i - 1 ' Avanzar al siguiente valor
                    Loop
        
                    If Not IsEmpty(WSA.Cells(i, 6).Value) Then
                        riskpercentageanterior = WSA.Cells(i, 6).Value
                        ' Configuraci—n de RiskPercentageA
                        RiskPercentage = riskpercentageanterior * risk
                        WSA.Cells(LRNSA + 1, 6).Value = RiskPercentage
                    End If
        
                ElseIf LUNCH = True Then
                    ' Comprobar que la celda (i,7) no estŽ vac’a
                    Do While IsEmpty(WSA.Cells(i, 7).Value) And i > 1
                        i = i - 1 ' Avanzar al siguiente valor
                    Loop
        
                    If Not IsEmpty(WSA.Cells(i, 7).Value) Then
                        riskpercentageanterior = WSA.Cells(i, 7).Value
                        ' Configuraci—n de RiskPercentageB
                        RiskPercentage = riskpercentageanterior * risk
                        WSA.Cells(LRNSA + 1, 7).Value = RiskPercentage
                    End If
                End If
        
                ' Configuraci—n de #Trade
                WSA.Cells(LRNSA + 1, 1).Value = WSA.Cells(i, 1).Value + 1
        
                Exit For ' Una vez que encuentre la œltima coincidencia, salimos del bucle
            End If
        Next i
    
    End If
    


    If RiskPercentage < 1 Then
        X = 0.03
    Else
        X = 0.1
    End If

' Aplicar filtros en la hoja de datos
    WS.Range("A1").AutoFilter Field:=1, Criteria1:=CRITERIA_MARKET
    WS.Range("A1").AutoFilter Field:=2, Criteria1:=CRITERIA_ACCOUNT
    WS.Range("A1").AutoFilter Field:=3, Criteria1:=CRITERIA_MARKETDATA, VisibleDropDown:=True

    ' Obtener la œltima fila
    LastRow = WS.Cells(WS.Rows.Count, "A").End(xlUp).ROW
    ' Inicializaci—n de variables
    J = 2
    ' Inicializar la colecci—n para almacenar los valores
    Set ListaValores = New Collection
    
    ' Recorrer las celdas visibles de la columna E para saber los simbolos
    On Error Resume Next ' Si no hay celdas visibles, evitar‡ un error
    For Each Celda In WS.Range("E2:E" & WS.Cells(WS.Rows.Count, "E").End(xlUp).ROW).SpecialCells(xlCellTypeVisible)
        ' Asegurarse de que la celda no estŽ vac’a
        If Celda.Value <> "" Then
            ' Agregar el valor de la columna E a la lista
            ListaValores.Add Celda.Value
        End If
    Next Celda
    On Error GoTo 0 ' Desactivar manejo de errores
    ' Mostrar los valores guardados (opcional)
    Dim l As Variant
    For Each l In ListaValores
        Debug.Print l ' Imprime los valores en la ventana de inmediato de VBA
    Next l
    
    ' Procesar filas visibles
    For i = 2 To LastRow
        If Not WS.Rows(i).Hidden Then
            Asset_Symbol = WS.Cells(i, 1).Value
            Broker = WS.Cells(i, 2).Value

            If Asset_Symbol = CRITERIA_MARKET And Broker = CRITERIA_ACCOUNT Then
                Symbol = WS.Cells(i, 5).Value
                TickSize = WS.Cells(i, 7).Value
                tickValue = WS.Cells(i, 8).Value
                Comission = WS.Cells(i, 10).Value
                
                ' Calcular para cada valor de StopTicks
                For Each currentStopTick In STOP_TICKS_VALUES
                    For k = 1 To 2
                        RiskPercentage_ = RiskPercentage + (X * (k - 1))
                        currentRisk = (AccountSize.Value * RiskPercentage_) / 100
    
                        riskPerContract = tickValue * currentStopTick + Comission
                        OptimalContracts = Int(currentRisk / riskPerContract)
                        totalCapitalUsed = OptimalContracts * riskPerContract
                        realRisk = (totalCapitalUsed * 100) / AccountSize
                        potentialProfit = (OptimalContracts * tickValue * ((currentStopTick * RISKTOREWARD.Value) + r)) - (Comission * OptimalContracts)
                        target = (currentStopTick * RISKTOREWARD.Value) + r
                        PotentialLoss = (OptimalContracts * riskPerContract)
                        
                        ' Guardar resultados
                        With WSR
                            .Cells(J, 1).Value = CRITERIA_MARKET
                            .Cells(J, 2).Value = CRITERIA_ACCOUNT
                            .Cells(J, 3).Value = Symbol
                            .Cells(J, 4).Value = target
                            .Cells(J, 5).Value = currentStopTick
                            .Cells(J, 6).Value = riskPerContract
                            .Cells(J, 7).Value = OptimalContracts
                            .Cells(J, 8).Value = totalCapitalUsed
                            .Cells(J, 9).Value = realRisk
                            .Cells(J, 10).Value = potentialProfit
                            .Cells(J, 11).Value = PotentialLoss
                            .Cells(J, 12).Value = RiskPercentage_
                        End With
                        'MsgBox "target = (currentStopTick * RISKTOREWARD.Value)+r" & target & " = " & currentStopTick & " * " & RISKTOREWARD.Value & "." & r
                        J = J + 1
                    Next k
                Next currentStopTick
            End If
        End If
    Next i
    

    
    With WSA
        ' Copiar los valores
        .Cells(12, LCNS12 + 3).Value = "SL Size (Ticks)"
        .Cells(12, LCNS12 + 4).Value = "FUTURES TICKER"
        .Cells(12, LCNS12 + 5).Value = "Optimal Contract"
        .Cells(12, LCNS12 + 6).Value = "Target (Ticks)"
        .Cells(11, LCNS12 + 3).Value = CRITERIA_ACCOUNT
        
        ' Copiar el formato de la celda (1,1) a las celdas correspondientes
        .Cells(1, 1).Copy
        .Cells(12, LCNS12 + 3).PasteSpecial Paste:=xlPasteFormats
        .Cells(12, LCNS12 + 4).PasteSpecial Paste:=xlPasteFormats
        .Cells(12, LCNS12 + 5).PasteSpecial Paste:=xlPasteFormats
        .Cells(12, LCNS12 + 6).PasteSpecial Paste:=xlPasteFormats
        .Cells(11, LCNS12 + 3).PasteSpecial Paste:=xlPasteFormats
    End With
    ' Limpiar el portapapeles
    Application.CutCopyMode = False

'Desactivar las alertas
Application.DisplayAlerts = False
' Ordenar resultados y resaltar m‡xima ganancia
    WSR.Sort.SortFields.Clear
    WSR.Range("A1:K" & J - 1).Sort Key1:=WSR.Range("E1"), Header:=xlYes
  
    currentStopTickGroupStart = 2 ' Empezamos desde la fila 2
    p = 12
    
    combineStartRow = 0 ' Inicializamos las variables para combinar
    combineEndRow = 0

    ' Recorrer las filas de la hoja de datos
    For ROW = 2 To J - 1
        ' Verificamos si el grupo en la columna E cambia o estamos en la œltima fila
        If WSR.Cells(ROW, 5).Value <> WSR.Cells(ROW + 1, 5).Value Or ROW = J Then
            groupValue = WSR.Cells(ROW, 5).Value ' Valor del grupo actual en la columna E
            maxProfit = -1 ' Inicializamos el m‡ximo beneficio
            
            ' Buscar el valor m‡ximo en la columna J para el grupo actual
            For i = currentStopTickGroupStart To ROW
                If WSR.Cells(i, 5).Value = groupValue Then ' Asegurarse de que estamos en el grupo correcto
                    If WSR.Cells(i, 10).Value > maxProfit Then
                        maxProfit = WSR.Cells(i, 10).Value
                        maxProfitRow = i ' Guardamos la fila con el m‡ximo profit
                    End If
                End If
            Next i
            
            ' Resaltar la fila con el valor m‡ximo de profit en la columna J
            If maxProfitRow > 0 Then
                WSR.Range(WSR.Cells(maxProfitRow, 1), WSR.Cells(maxProfitRow, 12)).Interior.Color = RGB(255, 255, 0)
                
                ' Copiar los datos a la hoja activa (WSA)
                With WSA
                    p = p + 1 ' Asume que p se incrementa por cada fila copiada
                    .Cells(p, LCNS12 + 3).Value = WSR.Cells(maxProfitRow, 5).Value ' Copia valor de grupo
                    .Cells(p, LCNS12 + 4).Value = WSR.Cells(maxProfitRow, 3).Value ' Copia valor columna C
                    .Cells(p, LCNS12 + 5).Value = WSR.Cells(maxProfitRow, 7).Value ' Copia valor columna G
                    .Cells(p, LCNS12 + 6).Value = WSR.Cells(maxProfitRow, 4).Value ' Copia valor columna D

                    ' Si el valor en la columna LCNS12 + 5 es 0, se combinan las celdas
                    If .Cells(p, LCNS12 + 5).Value = 0 Then
                        If combineStartRow = 0 Then
                            combineStartRow = p ' Establece la fila inicial para combinar
                        End If
                        combineEndRow = p ' Continœa estableciendo la fila final para combinar
                    Else
                        ' Si ya hay filas para combinar, las combinamos
                        If combineStartRow > 0 And combineEndRow > 0 Then
                            .Range(.Cells(combineStartRow, LCNS12 + 4), .Cells(combineEndRow, LCNS12 + 5)).Merge
                            .Cells(combineStartRow, LCNS12 + 4).Value = "The risk is too high for this stop-loss size on this account."
                        End If
                        ' Restablece las variables para la siguiente secci—n
                        combineStartRow = 0
                        combineEndRow = 0
                    End If
                End With
            End If
            
            ' Establecer el nuevo inicio de grupo
            currentStopTickGroupStart = ROW + 1
        End If
    Next ROW
    

' Combina cualquier grupo restante despuŽs de que termine el ciclo
If combineStartRow > 0 And combineEndRow > 0 Then
    WSA.Range(WSA.Cells(combineStartRow, LCNS12 + 4), WSA.Cells(combineEndRow, LCNS12 + 5)).Merge
    WSA.Cells(combineStartRow, LCNS12 + 4).Value = "The risk is too high for this stop-loss size on this account."
End If
' Activar las alertas nuevamente
Application.DisplayAlerts = True


    With WSA.Cells
        .WrapText = True
        .HorizontalAlignment = xlCenter
        .VerticalAlignment = xlCenter
        .Font.Name = "Calibri"
    End With
    
'COLORES DE RELLENO TABLA OPTIMAL CONTRACTS
    For ROW = 13 To p
    
        With WSA.Range(WSA.Cells(ROW, LCNS12 + 3), WSA.Cells(ROW, LCNS12 + 5))
            .Font.Bold = True
            .Font.Underline = xlUnderlineStyleSingle
            .Font.size = 14
            .Font.FontStyle = "Calibri"
        End With
        
        WSA.Cells(ROW, LCNS12 + 6).Interior.Color = RGB(211, 211, 211)
        WSA.Cells(ROW, LCNS12 + 6).Font.size = 9
        WSA.Cells(ROW, LCNS12 + 3).Interior.Color = RGB(255, 0, 0)
        FirstLetter = Left(WSA.Cells(ROW, LCNS12 + 4).Value, 1)

        Select Case WSA.Cells(1, 2).Value
            Case "NASDAQ"
                COLOR1 = RGB(255, 192, 203)
                COLOR2 = RGB(255, 20, 147)
            Case "S&P 500"
            
                COLOR1 = RGB(152, 251, 152)
                COLOR2 = RGB(0, 255, 50)
                
            Case "GOLD"
                
                COLOR1 = RGB(218, 165, 32)
                COLOR2 = RGB(184, 134, 11)
                
            Case "CRUDE OIL"
                
                COLOR1 = RGB(176, 224, 230)
                COLOR2 = RGB(0, 191, 255)
                
            Case "DOW JONES"
                
                COLOR1 = RGB(140, 217, 236)
                COLOR2 = RGB(68, 199, 231)
                
        End Select
        
        If FirstLetter = "M" Then
            WSA.Range(WSA.Cells(ROW, LCNS12 + 4), WSA.Cells(ROW, LCNS12 + 5)).Interior.Color = COLOR1
        Else
            WSA.Range(WSA.Cells(ROW, LCNS12 + 4), WSA.Cells(ROW, LCNS12 + 5)).Interior.Color = COLOR2
        End If
    
        
    Next ROW
    
'COLORES DE RELLENO TABLA KELLY COLUMNA C
    Select Case WSA.Cells(LRNSC + 1, 3).Value
        Case "TRADEIFY"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(0, 128, 128)
            WSA.Cells(LRNSC + 1, 3).Font.Color = &HFFFFFF
        Case "TOPSTEP"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = &HFFFFFF
        Case "NEXGEN"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(255, 153, 0)
        Case "NINJA TRADER"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(255, 102, 0)
        Case "BLUSKY.PRO"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(100, 149, 237)
            WSA.Cells(LRNSC + 1, 3).Font.Color = &HFFFFFF
        Case "ONE UP TRADER"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(218, 112, 214)
        Case "FUTURES ELITE"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(128, 0, 128)
            WSA.Cells(LRNSC + 1, 3).Font.Color = &HFFFFFF
        Case "APEX"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(0, 0, 255)
            WSA.Cells(LRNSC + 1, 3).Font.Color = &HFFFFFF
        Case "MY FUNDED FUTURES"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(218, 165, 32)
            WSA.Cells(LRNSC + 1, 3).Font.Color = &HFFFFFF
        Case "TAKE PROFIT TRADER"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(34, 139, 34)
            WSA.Cells(LRNSC + 1, 3).Font.Color = &HFFFFFF
        Case "TICKTICK TRADER"
            WSA.Cells(LRNSC + 1, 3).Interior.Color = RGB(0, 255, 255)
            WSA.Cells(LRNSC + 1, 3).Font.Color = &H80000012
    End Select
   
If IsEmpty(WSA.Cells(11, 12)) Then
' Preguntar si desea generar el XML
    PREGUNTA = MsgBox("Do you want to generate the xml?", vbYesNo + vbQuestion)
    If PREGUNTA = vbYes Then
        MsgBox "Select the folder where you want to save the XML."
        ' Inicializar el FileDialog
        Set fd = Application.FileDialog(msoFileDialogFolderPicker)
        With fd
            .Title = "Obtener la ruta de una carpeta"
            .AllowMultiSelect = False
            .Show
    
            ' Obtener la ruta de la carpeta seleccionada
            Set items = fd.SelectedItems
            If items.Count = 0 Then
                MsgBox "The user don«t select the folder."
                Exit Sub
            
            Else
            
            rutaCarpeta = items.Item(1)
            ' Inicializar el FileSystemObject
            Set fso = CreateObject("Scripting.FileSystemObject")
            fechaHoy = Date ' Fecha actual (solo la parte de la fecha, sin la hora)
        
            ' Recorrer todos los archivos en la carpeta
            On Error Resume Next ' Para ignorar errores en caso de que algœn archivo no sea accesible
            For Each archivo In fso.GetFolder(rutaCarpeta).Files
                archivoXML = archivo.Name
        
                ' Verificar si el archivo es un archivo XML
                If LCase(fso.GetExtensionName(archivoXML)) = "xml" Then
                    fechaCreacion = archivo.DateCreated
                    ' Comparar solo las fechas (ignorando las horas)
                    If Int(fechaCreacion) <> fechaHoy Then
                        ' Si la fecha de creaci—n es distinta a la de hoy eliminar el archivo
                        archivo.Delete
                        ' MsgBox "Delete XML files: " & archivoXML
                    Else
                        ' Mostrar los valores guardados (opcional)
                        For Each o In ListaValores
                            nombredoc = Ini & " " & MDT & " " & Q & " " & o
                            If Left(archivoXML, Len(nombredoc)) = nombredoc Then
                               archivo.Delete
                            End If
                        Next o
                    End If
                End If
                
            
            Next archivo
                ' Configuraciones predeterminadas
                Set configurations = CreateObject("Scripting.Dictionary")
                configurations.Add "IsVisible", "true"
                configurations.Add "AreLinesConfigurable", "true"
                configurations.Add "ArePlotsConfigurable", "true"
                configurations.Add "BarsToLoad", "0"
                configurations.Add "DisplayInDataBox", "true"
                configurations.Add "From", "2099-12-01T00:00:00"
                configurations.Add "Panel", "0"
                configurations.Add "ScaleJustification", "Right"
                configurations.Add "ShowTransparentPlotsInDataBox", "false"
                configurations.Add "To", "1800-01-01T00:00:00"
                configurations.Add "Calculate", "OnBarClose"
                configurations.Add "Displacement", "0"
                configurations.Add "IsAutoScale", "true"
                configurations.Add "IsDataSeriesRequired", "false"
                configurations.Add "IsOverlay", "false"
                configurations.Add "MaximumBarsLookBack", "TwoHundredFiftySix"
                configurations.Add "Name", "AtmStrategy"
                configurations.Add "BarsRequiredToTrade", "0"
                configurations.Add "Category", "Atm"
                configurations.Add "ConnectionLossHandling", "KeepRunning"
                configurations.Add "DaysToLoad", "1"
                configurations.Add "DefaultQuantity", "1"
                configurations.Add "DisconnectDelaySeconds", "0"
                configurations.Add "EntriesPerDirection", "1"
                configurations.Add "EntryHandling", "AllEntries"
                configurations.Add "ExitOnSessionCloseSeconds", "0"
                configurations.Add "IncludeCommission", "false"
                configurations.Add "IsAggregated", "false"
                configurations.Add "OptimizationPeriod", "10"
                configurations.Add "SetOrderQuantity", "Strategy"
                configurations.Add "StartBehavior", "AdoptAccountPosition"
                configurations.Add "StopTargetHandling", "PerEntryExecution"
                configurations.Add "TestPeriod", "28"
                configurations.Add "TimeInForce", "Day"
                configurations.Add "CalculationMode", "Ticks"
                configurations.Add "ChaseLimit", "0"
                configurations.Add "InitialTickSize", "0"
                configurations.Add "IsChase", "false"
                configurations.Add "IsChaseIfTouched", "false"
                configurations.Add "IsTargetChase", "false"
                configurations.Add "ReverseAtStop", "false"
                configurations.Add "ReverseAtTarget", "false"
                configurations.Add "UseMitForProfit", "false"
                configurations.Add "UseStopLimitForStopLossOrders", "false"
                
                ' Recorrer las filas de la hoja
                For rowIndex = 13 To p
                'si la celda de optimal contracts no esta vacia entonces crea el archivo XML
                    If Not IsEmpty(WSA.Cells(rowIndex, LCNS12 + 5).Value) Then
                    ' Crear un nuevo documento XML
                    Set xmlDoc = CreateObject("MSXML2.DOMDocument")
                    Set root = xmlDoc.createElement("NinjaTrader")
                    xmlDoc.appendChild root
                    
                    ' Crear el nodo principal AtmStrategy
                    Set atmStrategy = xmlDoc.createElement("AtmStrategy")
                    atmStrategy.setAttribute "xmlns:xsd", "http://www.w3.org/2001/XMLSchema"
                    atmStrategy.setAttribute "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"
                    root.appendChild atmStrategy
                    
                    ' Agregar configuraciones predeterminadas
                    For Each key In configurations.Keys
                        Set configNode = xmlDoc.createElement(key)
                        configNode.Text = configurations(key)
                        atmStrategy.appendChild configNode
                    Next key
                    
                    ' Agregar Template
                    Set templateNode = xmlDoc.createElement("Template")
                    templateNode.Text = WSA.Cells(rowIndex, LCNS12 + 4).Value & " " & WSA.Cells(rowIndex, LCNS12 + 3).Value
                    atmStrategy.appendChild (templateNode)
                    
                    ' Agregar EntryQuantity
                    Set entryQuantityNode = xmlDoc.createElement("EntryQuantity")
                    entryQuantityNode.Text = WSA.Cells(rowIndex, LCNS12 + 5).Value
                    atmStrategy.appendChild (entryQuantityNode)
                    
                    ' Crear Brackets
                    Set brackets = xmlDoc.createElement("Brackets")
                    Set bracket = xmlDoc.createElement("Bracket")
                    brackets.appendChild bracket
                    
                    ' Agregar Quantity
                    Set quantityNode = xmlDoc.createElement("Quantity")
                    quantityNode.Text = WSA.Cells(rowIndex, LCNS12 + 5).Value
                    bracket.appendChild quantityNode
                    
                    ' Agregar StopLoss
                    Set stopLossNode = xmlDoc.createElement("StopLoss")
                    stopLossNode.Text = WSA.Cells(rowIndex, LCNS12 + 3).Value
                    bracket.appendChild stopLossNode
                    
                    ' Agregar Target (ajustar columna segœn corresponda)
                    Set targetNode = xmlDoc.createElement("Target")
                    targetNode.Text = WSA.Cells(rowIndex, LCNS12 + 6).Value
                    bracket.appendChild targetNode
                    
                    'Nombre del archivo XML
                    fileName = Ini & " " & MDT & " " & Q & " " & WSA.Cells(rowIndex, LCNS12 + 4).Value & " " & WSA.Cells(rowIndex, LCNS12 + 3).Value & ".xml"
                    atmStrategy.appendChild brackets
                    
                    'Si existe un archivo que sea del mismo d’a de creaci—n con el mismo nombre entonces lo elimina
                    If archivoXML = fileName Then
                        archivo.Delete
                    End If
                    
                    If IsNumeric(WSA.Cells(rowIndex, LCNS12 + 5).Value) Or WSA.Cells(rowIndex, LCNS12 + 5).Value > 0 Then
                        ' Guardar el archivo XML
                        xmlDoc.Save items.Item(1) & "\" & fileName
                    End If

                End If
                Next rowIndex
                
                MsgBox "XML files generated successfully."
            
            End If
        End With
    End If
   ' Preguntar si desea generar el archivo .ahk
    PREGUNTA4 = MsgBox("Do you want to generate the .ahk?", vbYesNo + vbQuestion)
    If PREGUNTA4 = vbYes Then
        MsgBox "Select the folder where you want to save the .ahk."
        
        ' Inicializar el FileDialog
        Set fd = Application.FileDialog(msoFileDialogFolderPicker)
        With fd
            .Title = "Obtain the folder path"
            .AllowMultiSelect = False
            .Show
            
            ' Obtener la ruta de la carpeta seleccionada
            Set items = fd.SelectedItems
            If items.Count = 0 Then
                MsgBox "The user didn't select the folder."
                Exit Sub
            Else
                rutaCarpeta = items.Item(1)
                ' Inicializar el FileSystemObject
                Set fso = CreateObject("Scripting.FileSystemObject")
                fechaHoy = Date ' Fecha actual (solo la parte de la fecha, sin la hora)
                
                ' Recorrer todos los archivos en la carpeta
                On Error Resume Next ' Para ignorar errores en caso de que algœn archivo no sea accesible
                For Each archivo In fso.GetFolder(rutaCarpeta).Files
                    archivoNombre = archivo.Name
                    
                    ' Verificar si el archivo es un archivo ahk
                    If LCase(fso.GetExtensionName(archivoNombre)) = "ahk" Then
                        fechaCreacion = archivo.DateCreated
                        
                        ' Comparar solo las fechas (ignorando las horas)
                        If Int(fechaCreacion) <> fechaHoy Then
                            ' Si la fecha de creaci—n es distinta a la de hoy, eliminar el archivo
                            archivo.Delete
                        End If
                            ' Mostrar los valores guardados (opcional)
                            For Each o In ListaValores
                                nombreAHK = Ini & " " & MDT & " " & Q & " " & CRITERIA_MARKET
                                If Left(archivoNombre, Len(nombreAHK)) = nombreAHK Then
                                   archivo.Delete
                                End If
                            Next o
                    End If
                Next archivo
 
                ' Generar el script para el archivo .ahk
                script = "#NoEnv" & vbCrLf & _
                         "#Warn" & vbCrLf & _
                         "#SingleInstance force" & vbCrLf & _
                         "SetTitleMatchMode, 2" & vbCrLf & _
                         "SetBatchLines, -1" & vbCrLf & vbCrLf & _
                         "#include " & rutaCarpeta & "\Lib\UIA_Interface.ahk" & vbCrLf & _
                         "; Presses the Buy button in any active window." & vbCrLf & _
                         "^+0::" & vbCrLf & _
                         "UIA := UIA_Interface()" & vbCrLf & _
                         "chartWin := UIA.ElementFromHandle(""A"")" & vbCrLf & _
                         "chartWin.FindFirstByNameAndType(""Buy"", ""Text"").Click(""Left"")" & vbCrLf & _
                         "return" & vbCrLf & vbCrLf & _
                         "; Presses the Sell button in any active window." & vbCrLf & _
                         "^+1::" & vbCrLf & _
                         "UIA := UIA_Interface()" & vbCrLf & _
                         "chartWin := UIA.ElementFromHandle(""A"")" & vbCrLf & _
                         "chartWin.FindFirstByNameAndType(""Sell"", ""Text"").Click(""Left"")" & vbCrLf & _
                         "return" & vbCrLf & vbCrLf

                LastRow = WSA.Cells(WSA.Rows.Count, LCNS12 + 3).End(xlUp).ROW
                ' Inicializamos la variable para las letras
                Dim letra As String
                letra = "a" ' Empezamos con la letra "a"
                
                ' Recorrer las filas de la hoja
                For rowIndex = 13 To LastRow
                    
                ' Asignar la letra al atajo de teclado y luego avanzar a la siguiente letra
                script = script & "; Presses the Strategy button then selects """ & Ini & " " & MDT & " " & Q & " " & WSA.Cells(rowIndex, LCNS12 + 4).Value & " " & WSA.Cells(rowIndex, LCNS12 + 3).Value & """ in any active window" & vbCrLf & _
                            "^+" & letra & "::" & vbCrLf & _
                            "UIA := UIA_Interface()" & vbCrLf & _
                            "resolveStrategy := UIA.ElementFromHandle(""A"")" & vbCrLf & _
                            "resolveStrategy.FindFirstBy(""AutomationId=BasicEntryControlATMStrategySelector OR AutomationId=ChartTraderControlATMStrategySelector"").Click(""Left"")" & vbCrLf & _
                            "AutomationId=ChartTraderControlATMStrategySelector"").Click(""Left"")" & vbCrLf & _
                            "Sleep, 200" & vbCrLf & _
                            "resolveStrategy.FindFirstByNameAndType(""" & Ini & " " & MDT & " " & Q & " " & WSA.Cells(rowIndex, LCNS12 + 4).Value & " " & WSA.Cells(rowIndex, LCNS12 + 3).Value & """, ""Text"").Click(""Left"")" & vbCrLf & _
                            "return" & vbCrLf & vbCrLf
                        ' Avanzar a la siguiente letra (si es "z", vuelve a "a")
                        If letra = "z" Then
                            letra = "a"
                        Else
                            letra = Chr(Asc(letra) + 1) ' Incrementa la letra
                        End If
    
                Next rowIndex
    
                'Nombre del archivo AHK
                fileName = "\" & Ini & " " & MDT & " " & Q & " " & CRITERIA_MARKET & " Script.ahk"
                
                ' Crear y escribir en el archivo AHK
                archivoAHK = FreeFile
                rutaArchivo = rutaCarpeta & fileName ' Definir la ruta completa del archivo AHK
                
                Open rutaArchivo For Output As archivoAHK
                Print #archivoAHK, script
                Close archivoAHK
        
                'Si existe un archivo que sea del mismo d’a de creaci—n con el mismo nombre entonces lo elimina
                If archivoAHK = fileName Then
                    archivo.Delete
                End If
        
                MsgBox "AHK files generated successfully."
            End If
        End With
    End If
End If
    
    If Not IsEmpty(WSA.Cells(11, 12)) Then
                
        'Crea la columna ratio
        WSA.Cells(12, LCNS12 + 7).Value = "RATIO"
        ' Copiar el formato de la celda (1, 1)
        WSA.Cells(1, 1).Copy
        WSA.Cells(12, LCNS12 + 7).PasteSpecial Paste:=xlPasteFormats
        Application.CutCopyMode = False ' Limpiar el modo de copia
                
        For ROW = 13 To p
            'Calcula el ratio solo si la cuenta maestra tiene un # y los valores de la cuenta nueva son #, se sabe que son #>0 pq si es =0 tiene texto
            If IsNumeric(WSA.Cells(ROW, 12)) And IsNumeric(WSA.Cells(ROW, LCNS12 + 5)) Then
                WSA.Cells(ROW, LCNS12 + 7) = Round((WSA.Cells(ROW, LCNS12 + 5) / WSA.Cells(ROW, 12)), 2)
            End If
        Next ROW
            
            'Formato de la columna ratio
            With WSA.Range(Cells(13, LCNS12 + 7), Cells(p, LCNS12 + 7))
                .Interior.Color = RGB(0, 0, 0)
                .Font.Color = RGB(255, 255, 255)
                .Font.Bold = True
                .HorizontalAlignment = xlCenter
                .VerticalAlignment = xlCenter
            End With
   
        pregunta2 = MsgBox("Do you need to calculate another account?", vbYesNo + vbQuestion)
            
            'Comprobaci—n si se eligi— calcular m‡s de una cuenta o si desea calcular otra cuenta
            If pregunta2 = vbYes Then
                MARKET_BOX.Value = CRITERIA_MARKET
                AccountSize.Value = ""
                'Repite el proceso
                OptimalContracts_Click
            Else

' Preguntar si desea generar el XML
PREGUNTA3 = MsgBox("Do you want to generate the xml with ratios?", vbYesNo + vbQuestion)

If PREGUNTA3 = vbYes Then
    MsgBox "Select the folder where you want to save the XML."
    
    ' Inicializar el FileDialog
    Set fd = Application.FileDialog(msoFileDialogFolderPicker)
    With fd
        .Title = "Obtener la ruta de una carpeta"
        .AllowMultiSelect = False
        .Show
        
        ' Obtener la ruta de la carpeta seleccionada
        Set items = fd.SelectedItems
        
        If items.Count = 0 Then
            MsgBox "The user didn't select the folder."
            Exit Sub
        Else
            rutaCarpeta = items.Item(1)
            
            ' Inicializar el FileSystemObject
            Set fso = CreateObject("Scripting.FileSystemObject")
            fechaHoy = Date ' Fecha actual (solo la parte de la fecha, sin la hora)
            
            ' Recorrer todos los archivos en la carpeta
            On Error Resume Next ' Para ignorar errores en caso de que algœn archivo no sea accesible
            For Each archivo In fso.GetFolder(rutaCarpeta).Files
                archivoXML = archivo.Name
                
                ' Verificar si el archivo es un archivo XML
                If LCase(fso.GetExtensionName(archivoXML)) = "xml" Then
                    fechaCreacion = archivo.DateCreated
                    
                    ' Comparar solo las fechas (ignorando las horas)
                    If Int(fechaCreacion) <> fechaHoy Then
                        ' Si la fecha de creaci—n es distinta a la de hoy eliminar el archivo
                        archivo.Delete
                        ' MsgBox "Delete XML files: " & archivoXML
                    Else
                        ' Mostrar los valores guardados (opcional)
                        For ROW = 13 To p
                            nombredoc = WSA.Cells(ROW, 10) & "T" & Q & "-" & BEI
                            If Left(archivoXML, Len(nombredoc)) = nombredoc Then
                                archivo.Delete
                            End If
                        Next ROW
                    End If
                End If
            Next archivo
        End If
    End With
End If

' Recorrer todas las filas
For ROW = 13 To p
    ' Obtener el nombre del archivo (nombredoc)
    nombredoc = WSA.Cells(ROW, 10) & "T" & Q & "-" & BEI

    ' Crear el contenido XML
    StopLossTicks = WSA.Cells(ROW, 10).Value
    TakeProfitTicks = WSA.Cells(ROW, 13).Value
    MasterInstrument = BEI & " 03-25"
    groupName = WSA.Cells(ROW, 10).Value & "T" & Q & "-" & BEI
    AccInstrument1 = WSA.Cells(ROW, 11).Value & " 03-25"
    AccRatio1 = 1

    If WSA.Cells(11, 16) = "MY FUNDED FUTURES" Then
        nr = 20
        n = 17
    ElseIf WSA.Cells(11, 23) = "MY FUNDED FUTURES" Then
        nr = 27
        n = 24
    End If
    
    If WSA.Cells(11, 16) = "TRADEIFY" Then
        mr = 20
        m = 17
    ElseIf WSA.Cells(11, 23) = "TRADEIFY" Then
        mr = 27
        m = 24
    End If

    AccInstrument2 = WSA.Cells(ROW, m).Value & " 03-25"
    AccRatio2 = WSA.Cells(ROW, mr).Value
    AccInstrument9 = WSA.Cells(ROW, n).Value & " 03-25"
    AccRatio9 = WSA.Cells(ROW, nr).Value

    ' Cambiar las comas por puntos en los valores
    AccRatio2 = Replace(AccRatio2, ",", ".")
    AccRatio9 = Replace(AccRatio9, ",", ".")

            ' Crear el contenido XML
            Dim xmlContent As String
            xmlContent = "<?xml version=""1.0"" encoding=""utf-8""?>" & vbCrLf
            xmlContent = xmlContent & "<NinjaTrader>" & vbCrLf
            xmlContent = xmlContent & "  <Stc2>" & vbCrLf
            xmlContent = xmlContent & "    <Stc2 xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"">" & vbCrLf
            xmlContent = xmlContent & "      <IsVisible>true</IsVisible>" & vbCrLf
            xmlContent = xmlContent & "      <AreLinesConfigurable>true</AreLinesConfigurable>" & vbCrLf
            xmlContent = xmlContent & "      <ArePlotsConfigurable>true</ArePlotsConfigurable>" & vbCrLf
            xmlContent = xmlContent & "      <BarsPeriodSerializable>" & vbCrLf
            xmlContent = xmlContent & "        <BarsPeriodTypeSerialize>4</BarsPeriodTypeSerialize>" & vbCrLf
            xmlContent = xmlContent & "        <BaseBarsPeriodType>Minute</BaseBarsPeriodType>" & vbCrLf
            xmlContent = xmlContent & "        <BaseBarsPeriodValue>1</BaseBarsPeriodValue>" & vbCrLf
            xmlContent = xmlContent & "        <VolumetricDeltaType>BidAsk</VolumetricDeltaType>" & vbCrLf
            xmlContent = xmlContent & "        <MarketDataType>Last</MarketDataType>" & vbCrLf
            xmlContent = xmlContent & "        <PointAndFigurePriceType>Close</PointAndFigurePriceType>" & vbCrLf
            xmlContent = xmlContent & "        <ReversalType>Tick</ReversalType>" & vbCrLf
            xmlContent = xmlContent & "        <Value>1</Value>" & vbCrLf
            xmlContent = xmlContent & "        <Value2>1</Value2>" & vbCrLf
            xmlContent = xmlContent & "      </BarsPeriodSerializable>" & vbCrLf
            xmlContent = xmlContent & "      <BarsToLoad>0</BarsToLoad>" & vbCrLf
            xmlContent = xmlContent & "      <DisplayInDataBox>false</DisplayInDataBox>" & vbCrLf
            xmlContent = xmlContent & "      <From>2099-12-01T00:00:00</From>" & vbCrLf
            xmlContent = xmlContent & "      <Panel>-1</Panel>" & vbCrLf
            xmlContent = xmlContent & "      <ScaleJustification>Right</ScaleJustification>" & vbCrLf
            xmlContent = xmlContent & "      <ShowTransparentPlotsInDataBox>false</ShowTransparentPlotsInDataBox>" & vbCrLf
            xmlContent = xmlContent & "      <To>1800-01-01T00:00:00</To>" & vbCrLf
            xmlContent = xmlContent & "      <Calculate>OnEachTick</Calculate>" & vbCrLf
            xmlContent = xmlContent & "      <Displacement>0</Displacement>" & vbCrLf
            xmlContent = xmlContent & "      <IsAutoScale>true</IsAutoScale>" & vbCrLf
            xmlContent = xmlContent & "      <IsDataSeriesRequired>true</IsDataSeriesRequired>" & vbCrLf
            xmlContent = xmlContent & "      <IsOverlay>true</IsOverlay>" & vbCrLf
            xmlContent = xmlContent & "      <Lines />" & vbCrLf
            xmlContent = xmlContent & "      <MaximumBarsLookBack>TwoHundredFiftySix</MaximumBarsLookBack>" & vbCrLf
            xmlContent = xmlContent & "      <Name>Smart Trade Copier Two</Name>" & vbCrLf
            xmlContent = xmlContent & "      <Plots />" & vbCrLf
            xmlContent = xmlContent & "      <SelectedValueSeries>0</SelectedValueSeries>" & vbCrLf
            xmlContent = xmlContent & "      <InputPlot>0</InputPlot>" & vbCrLf
            xmlContent = xmlContent & "      <IsTradingHoursBreakLineVisible>true</IsTradingHoursBreakLineVisible>" & vbCrLf
            xmlContent = xmlContent & "      <DrawHorizontalGridLines>true</DrawHorizontalGridLines>" & vbCrLf
            xmlContent = xmlContent & "      <DrawVerticalGridLines>true</DrawVerticalGridLines>" & vbCrLf
            xmlContent = xmlContent & "      <DrawOnPricePanel>false</DrawOnPricePanel>" & vbCrLf
            xmlContent = xmlContent & "      <PaintPriceMarkers>false</PaintPriceMarkers>" & vbCrLf
            xmlContent = xmlContent & "      <ChartHashCodeDeserialized>0</ChartHashCodeDeserialized>" & vbCrLf
            xmlContent = xmlContent & "      <IndicatorId>223</IndicatorId>" & vbCrLf
            xmlContent = xmlContent & "      <MaxSerialized>0</MaxSerialized>" & vbCrLf
            xmlContent = xmlContent & "      <MinSerialized>0</MinSerialized>" & vbCrLf
            xmlContent = xmlContent & "      <ZOrder>-2147483648</ZOrder>" & vbCrLf
            xmlContent = xmlContent & "      <version>1.0.1  -==-  Lifetime </version>" & vbCrLf
            xmlContent = xmlContent & "      <buttonSize>25</buttonSize>" & vbCrLf
            xmlContent = xmlContent & "      <buttonFontSize>12</buttonFontSize>" & vbCrLf
            xmlContent = xmlContent & "      <TextBrushSerialize>&lt;SolidColorBrush xmlns=""http://schemas.microsoft.com/winfx/2006/xaml/presentation""&gt;#FFFFFAFA&lt;/SolidColorBrush&gt;</TextBrushSerialize>" & vbCrLf
            xmlContent = xmlContent & "      <fontSize>14</fontSize>" & vbCrLf
            xmlContent = xmlContent & "      <keepItOn>false</keepItOn>" & vbCrLf
            xmlContent = xmlContent & "      <sendStopLimitOrders>false</sendStopLimitOrders>" & vbCrLf
            xmlContent = xmlContent & "      <groupFlatten>false</groupFlatten>" & vbCrLf
            xmlContent = xmlContent & "      <UseBrakets>false</UseBrakets>" & vbCrLf
            xmlContent = xmlContent & "      <StopLossTicks>" & StopLossTicks & "</StopLossTicks>" & vbCrLf
            xmlContent = xmlContent & "      <TakeProfitTicks>" & TakeProfitTicks & "</TakeProfitTicks>" & vbCrLf
            xmlContent = xmlContent & "      <useSmartRRlineEntry>false</useSmartRRlineEntry>" & vbCrLf
            xmlContent = xmlContent & "      <useEntryLine>false</useEntryLine>" & vbCrLf
            xmlContent = xmlContent & "      <rrRatio>2</rrRatio>" & vbCrLf
            xmlContent = xmlContent & "      <rrHighlight>true</rrHighlight>" & vbCrLf
            xmlContent = xmlContent & "      <rOpacity>15</rOpacity>" & vbCrLf
            xmlContent = xmlContent & "      <masterInstrument>" & MasterInstrument & "</masterInstrument>" & vbCrLf
            xmlContent = xmlContent & "      <groupName>" & groupName & "</groupName>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_1>" & AccInstrument1 & "</AccInstrument_1>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_1>" & AccRatio1 & "</AccRatio_1>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_1>false</Enable_1>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_2>" & AccInstrument2 & " </AccInstrument_2>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_2>" & AccRatio2 & " </AccRatio_2>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_2>false</Enable_2>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_3>" & AccInstrument2 & " </AccInstrument_3>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_3>" & AccRatio2 & " </AccRatio_3>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_3>false</Enable_3>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_4>" & AccInstrument2 & " </AccInstrument_4>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_4>" & AccRatio2 & " </AccRatio_4>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_4>false</Enable_4>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_5>" & AccInstrument2 & "</AccInstrument_5>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_5>" & AccRatio2 & "  </AccRatio_5>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_5>false</Enable_5>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_6>" & AccInstrument2 & "</AccInstrument_6>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_6>" & AccRatio2 & " </AccRatio_6>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_6>false</Enable_6>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_7>" & AccInstrument2 & " </AccInstrument_7>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_7>" & AccRatio2 & "  </AccRatio_7>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_7>false</Enable_7>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_8>" & AccInstrument2 & "</AccInstrument_8>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_8>" & AccRatio2 & " </AccRatio_8>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_8>false</Enable_8>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_9>" & AccInstrument9 & "</AccInstrument_9>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_9>" & AccRatio9 & "  </AccRatio_9>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_9>false</Enable_9>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_10>" & AccInstrument9 & " </AccInstrument_10>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_10>" & AccRatio9 & "</AccRatio_10>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_10>false</Enable_10>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_11>" & AccInstrument9 & " </AccInstrument_11>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_11>" & AccRatio9 & " </AccRatio_11>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_11>false</Enable_11>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_12>" & AccInstrument9 & " </AccInstrument_12>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_12>" & AccRatio9 & "</AccRatio_12>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_12>false</Enable_12>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_13></AccInstrument_13>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_13>0.1</AccRatio_13>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_13>false</Enable_13>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_14></AccInstrument_14>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_14>0.1</AccRatio_14>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_14>false</Enable_14>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_15></AccInstrument_15>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_15>0.1</AccRatio_15>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_15>false</Enable_15>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_16></AccInstrument_16>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_16>0.1</AccRatio_16>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_16>false</Enable_16>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_17></AccInstrument_17>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_17>0.1</AccRatio_17>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_17>false</Enable_17>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_18></AccInstrument_18>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_18>0.1</AccRatio_18>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_18>false</Enable_18>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_19></AccInstrument_19>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_19>0.1</AccRatio_19>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_19>false</Enable_19>" & vbCrLf
            xmlContent = xmlContent & "      <AccInstrument_20></AccInstrument_20>" & vbCrLf
            xmlContent = xmlContent & "      <AccRatio_20>0.1</AccRatio_20>" & vbCrLf
            xmlContent = xmlContent & "      <Enable_20>false</Enable_20>" & vbCrLf
            xmlContent = xmlContent & "    </Stc2>" & vbCrLf
            xmlContent = xmlContent & "  </Stc2>" & vbCrLf
            xmlContent = xmlContent & "</NinjaTrader>" & vbCrLf

    ' Crear la ruta del archivo (combinar la carpeta seleccionada y el nombre del archivo)
    archivoXML = rutaCarpeta & "\" & nombredoc & ".xml"

    ' Guardar el contenido del XML en el archivo
    Dim fileNumber As Integer
    fileNumber = FreeFile ' Obtener un nœmero de archivo libre
    Open archivoXML For Output As #fileNumber
    Print #fileNumber, xmlContent ' Escribir el contenido del XML en el archivo
    Close #fileNumber ' Cerrar el archivo
Next ROW

MsgBox "XML files generated successfully."
            End If
    Else
        PREGUNTA1 = MsgBox("Do you need to calculate more than one account?", vbYesNo + vbQuestion)
        
            'Si desea calcular m‡s de una cuenta :
            If PREGUNTA1 = vbYes Then
            
                'Crea encabezado
                WSA.Cells(11, LCNS12 + 5) = "MASTER ACCOUNT"
                ' Copiar el formato de la celda (1, 1)
                WSA.Cells(1, 1).Copy
                WSA.Cells(11, LCNS12 + 5).PasteSpecial Paste:=xlPasteFormats
                Application.CutCopyMode = False ' Limpiar el modo de copia

                'Comprobaci—n si se eligi— calcular m‡s de una cuenta o si desea calcular otra cuenta
                MARKET_BOX.Value = CRITERIA_MARKET
                AccountSize.Value = ""
                
                'Repite el proceso
                OptimalContracts_Click
                
            End If
        
    End If
End Sub

Private Sub CommandButton1_Click()
RISK_MANAGER.Hide
Unload Me
    If ActiveSheet.AutoFilterMode Then
     ActiveSheet.AutoFilterMode = False
    End If

End Sub
