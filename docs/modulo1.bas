Modulo exportado del Excel original. Contiene macros empleadas en el modelo CW-DARMM para calcular contratos óptimos.
Attribute VB_Name = "M—dulo1"
Sub Bisel1()
    Dim w As Worksheet
    Dim wsOptimal As Worksheet
    
    ' Borrar el contenido de la hoja "OPTIMAL_CONTRACTS" de la columna J a la N, fila 2 hacia abajo
    On Error Resume Next
    Set wsOptimal = ActiveSheet
    On Error GoTo 0

    If Not wsOptimal Is Nothing Then
        ' Borrar el contenido y formato desde la columna I3 hacia abajo y hasta la columna XF
        wsOptimal.Range("J11:XF" & wsOptimal.Cells(wsOptimal.Rows.Count, "j").End(xlDown).ROW).Clear
    End If

    ' Mostrar el formulario RISK_MANAGER
    RISK_MANAGER.Show
End Sub



Sub jjj()
FirstLetter = Left(WSA.Cells(ROW, LCNS12 + 4).Value, 1)

        Select Case WSA.Cells(1, 2).Value
            Case "NASDAQ"
                If FirstLetter = "M" Then
                    COLOR1 = RGB(255, 192, 203)
                    COLOR2 = RGB(255, 20, 147)
                End If
            Case "S&P 500"
                If FirstLetter = "M" Then
                    COLOR1 = RGB(152, 251, 152)
                    COLOR2 = RGB(0, 255, 50)
                End If
            Case "GOLD"
                If FirstLetter = "M" Then
                    COLOR1 = RGB(218, 165, 32)
                    COLOR2 = RGB(184, 134, 11)
                End If
            Case "CRUDE OIL"
                If FirstLetter = "M" Then
                    COLOR1 = RGB(176, 224, 230)
                    COLOR2 = RGB(0, 191, 255)
                End If
            Case "DOW JONES"
                If FirstLetter = "M" Then
                    COLOR1 = RGB(140, 217, 236)
                    COLOR2 = RGB(68, 199, 231)
                End If
        End Select
        
        If FirstLetter = "M" Then
            WSA.Range(WSA.Cells(ROW, LCNS12 + 4), WSA.Cells(ROW, LCNS12 + 5)).Interior.Color = COLOR1
        Else
            WSA.Range(WSA.Cells(ROW, LCNS12 + 4), WSA.Cells(ROW, LCNS12 + 5)).Interior.Color = COLOR2
        End If
    
End Sub

