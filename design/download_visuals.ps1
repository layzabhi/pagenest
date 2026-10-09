$items = @{
  'app_icon.png' = 'https://lh3.googleusercontent.com/aida/AEtjO1WprR7vXGRwpl9uaZDvtjFpBigIMzdovrYMp8qeunMrddK5W2P-uKexZkejM25UwhD22KQA7G8MKWiIz8-GndePXFvOmh3w7ZhEUQfgKITBQvjpYBpt_DCXS8PFSpZfphohnWMQaqf0uYJw7ufCduOiO6LmXr6ipVrkgPvKQrHh_Y5FS-wmo_ahC-YlF3U3D4Z6vsGgW5l13fhXBK4K-YS4-rVls9JXmnzcAsskYX7Up79nHHq4BK_XWJA'
  'brand_emblem.png' = 'https://lh3.googleusercontent.com/aida/AEtjO1UYzc5yRtSHF-N92uGIi8K89DKj6xSr_GCUnG5eypqC1WwXq0MuRSZ2-u9iixiwbN9NyAJ5DCjN7kUNXOqwWpJCs3NmycNalKP8FUKyavN85nXdrdzfwCgtyox9lwxVqyW3IQBJdcED53qEpLyxt87VOaAEDfPchNMDrtnqhgJlKW8Y5rKvEbz7GGFsfgGZVHI6Bj0lIbg40ItTlkSSpoSBQuWVNaWy1di_vmwqIVQxexdPEkY0oE5nMUsT5sbhu58QXmf9U0EICw'
  'library_documents.png' = 'https://lh3.googleusercontent.com/aida/AEtjO1XRv7TXIO3thEfDu-rdKuVbZH_A3NncAvzKgMlUgOPXaoH5Vw8u6aZRG3qF7mG1B4bCw-G4v2meIZ7rg7P6KYkMX2antbvgNYGFiZOuRHrpdU3yJRCMG3seYNfBTkPpWx0Ug5qf6GMtuATNjy2eZbon2Bw5pw-_3M8qcT97J6Av-KsjTA_QIEk_RS5KwlVeEy2-rGhw_2JWmA3WiXgw9I6rdvbXRebgdLo0Xquo9iqhD7rwgHEjitzOQQ'
  'pdf_reader.png' = 'https://lh3.googleusercontent.com/aida/AEtjO1V97uSlJU8RaKim9i5x6FpS55Pc6K2DKn-bInvbLUxKt9yQYNALwvEuwFH88eCVW3DhxzgPLKpsCjaoTWa54zzdhI7LP8VEBaY5InDyHqdN4Cf3sq32A01NfH0VD_ViKIeLKQ4ju0wW7jrL1OSsgcVa22HXtPPYNJ5pmA4i4TmNu_bNS8xZpu5Tjtz3cxRAzECH0KuO7v4S-jsu8uKDfhZpBav5gNRaMQI0ScPs8GhDyXTlrhwH5bN6r-A'
  'reading_history.png' = 'https://lh3.googleusercontent.com/aida/AEtjO1ViJAKYWGK92H-_7jC2N5FRtkfpgRaPlrkkZ93ZVmcdf_iEBLcH4fQsEUpwlOzHsGNcsR0cKYagAJRb6iin4q17V6noH0GowM9pg-ti_YXwhIJEkK25dq88nbz0DcMuQVspaz7ICiLmde1XSJLMAIFYmxd40LG-EbeeNf2v0T72SlGTv2ZBVUS_LOfGzgHNwIBrO6QAFpSG9jmJ8fIEyi2b69qXXr4F81HLpz4VXE-f51Y5Sz5AwLXtEDI'
  'themes_library.png' = 'https://lh3.googleusercontent.com/aida/AEtjO1WS7WeQFyW9j1-rTswOMsGL3csXZNFD_TgnrAJ18feFMQokXSINT0sTMhwr1vZnYwwdLHt4cPYOyyF3rIUsZLlQSBZULF8dAbChVbdqX8fR1F--lgz6CHt_vw2rtizDZoT_7OiNM7fxM_eANDEdkX59qXMsHgDD95qrmu9w7IQdAvTL2ikWywoSiriljMgnJVuH5-LFZ1eaLl5xjEqrdrFiP0x1QnLuF1prjE2YdWgrUQU-Shyv_pasn-I'
  'theme_editor.png' = 'https://lh3.googleusercontent.com/aida/AEtjO1Utpnz6elpT9UJfWD58a0y6HmIlZohVf_inOIu4bAneMz4K5qyfdEfMHvG-pLckGd1CzHBLRxuIuOwxKVLRMmND1untEPJA6Dn8GVVPNi-1ZRV3oC7UJQWFUV1ci5kZg5Jez7IYb6hh3Nnsd9TzWwKMUrq4y7GNjNyQ4AvTos2x9cM2pYE-mQOTo0sGzsYn7kk5AZJagyjbhQGEn2RotZF4oqnJnCEC7SBMKRxh1nJ_HRftlFGrcSRaR-Y'
}

$destDir = 'c:\All Projects\PageNest\design\screenshots'
$artifactDir = 'C:\Users\Abhi\.gemini\antigravity-ide\brain\68da7fe4-ddea-4889-8c08-e5719355c71b'

if (!(Test-Path $destDir)) {
  New-Item -ItemType Directory -Force -Path $destDir | Out-Null
}

foreach ($entry in $items.GetEnumerator()) {
  $file1 = Join-Path $destDir $entry.Key
  $file2 = Join-Path $artifactDir $entry.Key
  Write-Host "Downloading $($entry.Key)..."
  Invoke-WebRequest -Uri $entry.Value -OutFile $file1
  Copy-Item -Path $file1 -Destination $file2 -Force
}
Write-Host "All assets downloaded and synchronized successfully!"
