package com.example.internet_speed_testing

import java.math.BigDecimal

data class ProgressionModel(var progressTotal: Float = 0f,
                            var progressDownload: Float = 0f,
                            var progressUpload: Float = 0f,
                            var progressUploadCompletion: Boolean = false,
                            var progressDownloadCompletion: Boolean = false,
                            var progressCompletion: Boolean = false,
                            var uploadSpeed: BigDecimal = BigDecimal(0),
                            var downloadSpeed: BigDecimal = BigDecimal(0)
)
